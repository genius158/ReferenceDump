# reference-plugin
dump new出来的对象个数

```
日志

com.yan.referencecounttest.Test3 对像有四个
CLASS - com.yan.referencecounttest.Test3  4
      由Test2的构造函数创建了两个
----> com.yan.referencecounttest.Test2#<init>(I)V : 2
      由MainActivity的onCreate函数创建了一个
----> com.yan.referencecounttest.MainActivity#onCreate(LBundle;)V : 1
      由Test3的test3函数创建了一个
----> com.yan.referencecounttest.Test3#test3()V : 1
new出来的总个数
AllCount ----> 2005 
END--------------------------------------------------
```

## how to use 
in project mode
```
    classpath 'com.yan.referencecount:reference_plugin:1.0.5'
```
 in app module
```
apply plugin: 'reference-plugin'

referenceExt {
    logEnable = true
    // 是否在代码里插入方法名，提高性能，但是会暴露方法的原本信息
    listenerWithMethodDetail = true
    // 插件工作环境 DEBUG, RELEASE, ALWAYS, NEVER
    runVariant = 'DEBUG'
    // 只插桩到当前匹配的所有类 如果不为空，ignoreList失效 ，内部采用 contains(item)，来包涵匹配到的类
    foreList = ['com.burial.test.TestView']
    // 插桩忽略名单，不需要全类名，内部采用 contains(item)，来剔除配到的类
    // ignoreList = ['com.burial.test.MainActivity2']
    // 作用域 PROJECT,SUB_PROJECTS,EXTERNAL_LIBRARIES,TESTED_CODE,PROVIDED_ONLY
    // EXTERNAL_LIBRARIES lib范围，可能插桩失败
    scopes = ['PROJECT']
}

```

## in code 
```
  // 使用attachDumpView方法，添加dump浮窗
  ReferenceMgr.attachDumpView(getApplication());

  // 或者调用dump方法，打印日志
  ReferenceMgr.dump();

```

## License

    Copyright 2020 genius158

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.# burialPlugin
# ReferenceDump
