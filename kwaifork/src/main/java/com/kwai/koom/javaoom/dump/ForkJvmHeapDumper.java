package com.kwai.koom.javaoom.dump;

/**
 * Copyright 2020 Kwai, Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * A jvm hprof dumper which use fork and don't block main process.
 *
 * @author Rui Li <lirui05@kuaishou.com>
 */
public class ForkJvmHeapDumper {

    private static final String TAG = "ForkJvmHeapDumper";


    public ForkJvmHeapDumper() {
        System.loadLibrary("koom-java");
        initForkDump();
    }

    public boolean waitDumping(int pid) {
        waitPid(pid);
        return true;
    }

    /**
     * Init before do dump.
     *
     * @return init result
     */
    public native void initForkDump();

    /**
     * First do suspend vm, then do fork.
     *
     * @return result of fork
     */
    public native int trySuspendVMThenFork();

    /**
     * Wait process exit.
     *
     * @param pid waited process.
     */
    public native void waitPid(int pid);

    /**
     * Exit current process.
     */
    public native void exitProcess();

    /**
     * Resume the VM.
     */
    public native void resumeVM();

    /**
     * Dump hprof with hidden c++ API
     */
    public static native boolean dumpHprofDataNative(String fileName);
}
