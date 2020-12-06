# reference-plugin 
[demo.apk](https://github.com/genius158/ReferenceDump/blob/main/demo.apk)     

dump new出来的对象个数，大小   
内存占用大小目前使用jvm ti，android9及以上才支持，还是耗时的，可以先dump数量，再考虑是否需要dump 内存大小

```
日志
START--------------------------------------------------
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
END--------------------------------------------------
```

## how to use 
in project mode
```
    classpath 'com.yan.referencecount:reference_plugin:1.1.0'
```
 in app module
```
apply plugin: 'reference-plugin'

referenceExt {
    logEnable = true
    // 插件工作环境 DEBUG, RELEASE, ALWAYS, NEVER
    runVariant = 'DEBUG'
    // 有些第三次jar包混淆后由些特殊符号,可能会插桩失败,
    // 在EXTERNAL_LIBRARIES域下，只在librariesOnly匹配上的进行插桩
    librariesOnly = ['androidx.']
    // 只插桩到当前匹配的所有类 如果不为空，ignoreList失效 ，内部采用 contains(item)，来包涵匹配到的类
    foreList = ['com.burial.test.TestView']
    // 插桩忽略名单，不需要全类名，内部采用 contains(item)，来剔除配到的类
    // ignoreList = ['com.burial.test.MainActivity2']
    // 作用域 PROJECT,SUB_PROJECTS,EXTERNAL_LIBRARIES,TESTED_CODE,PROVIDED_ONLY
    // EXTERNAL_LIBRARIES lib范围，可能插桩失败
    scopes = ['PROJECT','SUB_PROJECTS']
}

```

## in code 
```
    // 主动调用dump方法，打印日志
    ReferenceMgr.dump();

    // 自定义dump回调，默认打印在logcat
    ReferenceMgr.setOnDumpListener(new OnDumpListener() {
            @Override
            public void onDump(@NotNull HashMap<Class<?>, ArrayList<ReferenceWeak<Object>>> hashMap) {
            }
        });
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
