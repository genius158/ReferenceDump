## 优化组件切换
组件化，这里就不啰嗦了，类似的文章太多了     
这里讲讲怎么**自动化** [核心gradle脚本插件链接](url)    
**大前提，所有模块都是Androidstudio默认的形式生成，不去手动增减任何build.gradle内部的用于判断组件的脚本**    
例如这样的:
`
if (isDebug.toBoolean()) {
    apply plugin: 'com.android.application'
} else {
    apply plugin: 'com.android.library'
}
`    
这样的:
`if(isDebug.toBoolean()) {
                manifest.srcFile 'src/debug/AndroidManifest.xml'
            } else {
                manifest.srcFile 'src/release/AndroidManifest.xml'
            }
`     
这样的:
`if(isNeedCahtModule.toBoolean()){implementation project(':chat')}`
#### - 切换library到application
apply plugin，执行了之后对应的配置会加到project.plugins，如果我们想在后续想移除`project.plugins.withType(LibraryPlugin){
    project.plugins.remove(it)
}`会报异常`java.lang.UnsupportedOperationException (no error message)`，就是说我们不能这么做，这部分目前没想到太好的方法，最终采用脚本替换的形式
```
if (hookInclude) {
    project.ant.replace(
            file: build,
            token: "apply plugin: 'com.android.library'",
            value: "apply plugin: 'com.android.application'"
    )
    // 1见下文
    insertAppConfig(project)
} else {
    project.ant.replace(
            file: build,
            token: "apply plugin: 'com.android.application'",
            value: "apply plugin: 'com.android.library'"
    )
}
```
顺便一说ant对应的一些命令（zip、unzip、copy，replace ect.）超级好用，replace甚至可以去改代码，在javac对应的任务里加入dependsOn，拿到任务的input，也就是一些对应build下的的java目录、文件，在java转class之前，去拿我们想改的类，做匹配修改，做到类似插桩的功能。      
(**tip：如果改非中间阶段的文件，在没有代码版本管理的情况下，是非常危险的，还是需要做一些备份恢复的处理**)

在看看注释1
```
// 注入applicationId
p.android.defaultConfig.applicationId "com.yan.application"

// 以下主要目的在组件模块下增加main平级的module目录插入manifest文件
def srcDir = new File(p.projectDir.canonicalPath + "/src")
def moduleDir = new File(srcDir.canonicalPath + "/module")
def moduleManifest = new File(p.projectDir.canonicalPath + "/src/module/AndroidManifest.xml")
if (!moduleDir.exists()) moduleDir.mkdirs()

def manifest = new File(srcDir.canonicalPath + "/main/AndroidManifest.xml")
if (!moduleManifest.exists()) {
    p.ant.copy(file: "$manifest.canonicalPath", tofile: "$moduleManifest.canonicalPath")
}

def moduleManifestTxt = moduleManifest.getText()
if (!moduleManifestTxt.contains("<application")) {
    moduleManifestTxt = moduleManifestTxt.replaceAll("\\s/\\s", "")
    moduleManifestTxt = moduleManifestTxt.replace("</manifest>",<application>..." )
    moduleManifest.write(moduleManifestTxt)
}

// 更改manifest为组件的
p.android.sourceSets.main.manifest.srcFile("src/module/AndroidManifest.xml")
```
上面的一段脚本入注释所写，为我们的组件自动生成了module目录，AndroidManifest指我们的模块里的AndroidManifest，且自动加上了application标签，同样的原理我们的组件化测试代码也可以用对应的脚本扔到module目录下，同时增加java配置指向，这样做主要是为了，当我们切换到lib模式，不会存在任何的代码文件的增加。      

#### - settings.gradle调整按需引入
```
apply from: "./moduleconfig.gradle"
if (!hookInclude || foreIncludeAll) {
    include ':test'
    ...
}
```
主要看moduleconfig.gradle 自定义部分
```
ext {
    // 强制全部依赖
    foreIncludeAll = false

    // 哪些模块需要自动lib转app
    autoModule = [":test", ":plugin2"]
}
// 只引入test模块
includeModule(":test", [":plugin2", ":router", ":moduleadapter"])
```
```
// 引入我们的组件，和这个组件对应的其他模块依赖
def includeModule(module, dependencies) {
	// hookInclude在setting文件中用于切换引用模式
    ext.hookInclude = true
    moduleListConfig[module] = dependencies
    include module
    dependencies.each { m -> include m }
}
```
如果我们用as自动生成了一个新的模块，那么它在setting脚本中自动加的include是不符合我们的要求的，所以加了一个判断，不符合提示调整脚本，当然利用ant的replace方法我们甚至可以自动调整依赖。
```
def checkSettingChange() {
    gradle.projectsLoaded {
        def rootPath = rootProject.projectDir.canonicalPath
        def setting = file(rootPath + "/settings.gradle")
        def settingText = setting.getText()
        def unModulePart = settingText.replaceAll("if.*hookInclude.*\\).*\\{[^}]+}", "")
        if (unModulePart.contains("include")) {
            throw new RuntimeException("请保证在settings.gra...")
        }
    }
}
```

#### 怎么依赖我们的组件
这里我新增了一个配置modelDependencies对应原本的dependencies
```
// 组件由这个域引入
modelDependencies {
    implementation project(":test")
}
```
脚本里对应以下部分
```
 def modelDependencies = project.getExtensions().create("modelDependencies", ModelDependencies.class)
 project.afterEvaluate {
     modelDependencies.implementations.each { module ->
         // moduleList 对应includeModule方法的moduleListConfig
         // 由modelDependencies引入的组件，判断它目前是否处在组件化模式，
         // 是则引入，不是则跳过
         if (moduleList.find { entity -> entity.key.contains(module.name) } == null) {
             project.dependencies { implementation module }
         }
     }
 }
```
## 组件内存优化
开发过程中内存可能内存的问题，**某个对象本身非常大，或者引用很大，可能需要去分析是不是存在内存分配问题，能不能优化；有些对象非常多，可能需要去分析是不是有个方法短时间内大量创建临时对象;当然还有老生常谈的内存泄露**。 

这些问题怎么能方便、及时的发现？   
一般我们怎么查内存问题的？   
利用profiler的dump Java heap？   
是的多数情况下我们是用的这个as自带的功能可以去查问题。能够发现一些大内存的对象，或者内存泄露，然后进行些后续的优化，**然而在组件开发过程中有什么问题呢？**     
1. dumpHprof给我们返回的对象太多了，我只想知道当前组件下的class文件所产生的内存快照，或者某个包名下的。
2. 或者更直观的，我不止想知道这个对象是由别的那个类哪个对象持有的，我还想知道这些对象是由哪个方法执行产生的。
3. 对于1，我们当然可以拿到内存快照后再过滤但是太慢了，使用profiler，对于大项目来说除了慢，甚至多触发几次，很快as就卡的不成样子，经常还遇到内存不足的情况，对于一个**生命周期很短的方法里瞬间创建了一堆对象**的情况很难判断到，这种情况恨不得几百毫秒能查看一次内存快照；使用leakcanary（这里不是说使用这个库，主要是表述我们自己dumpHprof，自己解析），新开线程或者进程去做解析，确实可以解决我们使用profiler的带来的影响开发工具本身性能的问题，但是dumpHprof后，解析过程的时间我们没法去缩短，想看解析出来的结果，还是常常需要等上10秒20秒，之后我们再去过滤统计，整个过程就拉的更长了。     

所以我想做到什么事情呢，我们开发过程中，**可以实时的查看当前模块下的对象创建和销毁，无需等待，或者说等待最少的时间，就可以时不时的去对比内存数据，发现问题的成本就非常低**，无聊了就可以点两下看看内存对比。
### 怎么做
做法其实很简单，就是利用插桩（asm指令级的操作），在**执行NEW这个操作命令**的时候，把这个对象链接到一个弱引用里，顺带加上执行的类执行的方法。当然对象还有反射生成的，但是开发过程中，我们绝大多数情况下都不会刻意的使用反射，很多时候都是避免使用反射，从而避免反射带来的一些性能问题，当然要记录反射生成的对象也可以，就是在对象的构造函数里插入代码去记录，当然对系统类我们就无能为力了。   
来看看对应的日志
```
// 对象总个数
RefCount ---->| 23 |<----
-
//StringBuilder存在14个
CLASS - java.lang.StringBuilder count: 14
// 耗4624字节，由App$onCreate$1#timer产生11个对象
----> size(byte): 4624 count: 11  
com.yan.referencecounttest.App$onCreate$1#timer(LBurialTimer;LString;LString;LString;J)V
//耗1064字节，由App$onCreate$1#<clinit>()V 产生3个对象
----> size(byte): 1064 count: 3  com.yan.referencecounttest.App$onCreate$1#<clinit>()V 
// 总共占5688字节
----> size(byte): 5688 *
```
怎么发现问题，就上面这段日志，每次查看内存日志App$onCreate$1#timer路径下的StringBuilder一致在增加，属于临时对象大量产生，我们就可以用一个成员变量来优化
```
BurialTimer.getTimer().setListener { ignore, className, methodName, des, cost ->
           if (cost>0){
               Log.e("BurialTimer","$cost   $className $methodName ")
           }
        }
```
优化
```
private val stringBuilder = StringBuilder()
override fun onCreate() {
	BurialTimer.getTimer().setListener { ignore, className, methodName, des, cost ->
            if (cost > 0) {
                stringBuilder.clear()
                stringBuilder.append(cost).append("   ").append(className).append("  ").append(methodName)
                Log.e("BurialTimer", stringBuilder.toString())
                stringBuilder.clear()
            }
        }
    }

```