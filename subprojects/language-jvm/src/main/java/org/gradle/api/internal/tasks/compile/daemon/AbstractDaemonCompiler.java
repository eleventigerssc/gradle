/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.tasks.compile.daemon;

import org.gradle.api.tasks.WorkResult;
import org.gradle.internal.UncheckedException;
import org.gradle.internal.nativeintegration.ProcessEnvironment;
import org.gradle.internal.nativeintegration.services.NativeServices;
import org.gradle.language.base.internal.compile.CompileSpec;
import org.gradle.language.base.internal.compile.Compiler;
import org.gradle.workers.internal.DaemonForkOptions;
import org.gradle.workers.internal.DefaultWorkResult;
import org.gradle.workers.internal.WorkSpec;
import org.gradle.workers.internal.Worker;
import org.gradle.workers.internal.WorkerFactory;
import org.gradle.workers.internal.WorkerProtocol;

import java.io.File;

public abstract class AbstractDaemonCompiler<T extends CompileSpec> implements Compiler<T> {
    private final Compiler<T> delegate;
    private final WorkerFactory workerFactory;
    private final File executionWorkingDir;

    public AbstractDaemonCompiler(File executionWorkingDir, Compiler<T> delegate, WorkerFactory workerFactory) {
        this.executionWorkingDir = executionWorkingDir;
        this.delegate = delegate;
        this.workerFactory = workerFactory;
    }

    public Compiler<T> getDelegate() {
        return delegate;
    }

    @Override
    public WorkResult execute(T spec) {
        DaemonForkOptions daemonForkOptions = toDaemonOptions(spec);
        Worker<WorkerCompileSpec<?>> worker = workerFactory.getWorker(CompilerDaemonServer.class, daemonForkOptions);
        DefaultWorkResult result = worker.execute(new WorkerCompileSpec<T>(delegate, spec, executionWorkingDir, daemonForkOptions.getDefaultWorkingDir()));
        if (result.isSuccess()) {
            return result;
        }
        throw UncheckedException.throwAsUncheckedException(result.getException());
    }

    protected abstract DaemonForkOptions toDaemonOptions(T spec);

    private static class WorkerCompileSpec<T extends CompileSpec> implements WorkSpec {
        private final Compiler<T> compiler;
        private final T spec;
        private final File executionWorkingDir;
        private final File idleWorkingDir;

        private WorkerCompileSpec(Compiler<T> compiler, T spec, File executionWorkingDir, File idleWorkingDir) {
            this.compiler = compiler;
            this.spec = spec;
            this.executionWorkingDir = executionWorkingDir;
            this.idleWorkingDir = idleWorkingDir;
        }

        @Override
        public String getDisplayName() {
            return compiler.getClass().getName();
        }

        public DefaultWorkResult compile() {
            return new DefaultWorkResult(compiler.execute(spec).getDidWork(), null);
        }

        public File getExecutionWorkingDir() {
            return executionWorkingDir;
        }

        public File getIdleWorkingDir() {
            return idleWorkingDir;
        }
    }

    public static class CompilerDaemonServer implements WorkerProtocol<WorkerCompileSpec<?>> {
        @Override
        public DefaultWorkResult execute(WorkerCompileSpec<?> spec) {
            ProcessEnvironment processEnvironment = NativeServices.getInstance().get(ProcessEnvironment.class);
            try {
                processEnvironment.maybeSetProcessDir(spec.getExecutionWorkingDir());
                return spec.compile();
            } catch (Throwable t) {
                return new DefaultWorkResult(true, t);
            } finally {
                processEnvironment.maybeSetProcessDir(spec.getIdleWorkingDir());
            }
        }
    }
}
