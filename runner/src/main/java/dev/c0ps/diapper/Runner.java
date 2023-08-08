/*
 * Copyright 2022 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.c0ps.diapper;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.ProvisionException;

import dev.c0ps.diapper.utils.ArgsParser;
import dev.c0ps.diapper.utils.ReflectionUtils;

public class Runner {

    private static final String THIS_PACKAGE = RunnerConfig.class.getPackageName();

    private final String[] basePkgs;
    private final ILogSettings logSettings;

    public Runner(String... basePkgs) {
        this(new NoLogSettings(), basePkgs);
    }

    public Runner(ILogSettings logSettings, String... basePkgs) {
        this.basePkgs = basePkgs;
        this.logSettings = logSettings;
    }

    public void run(String[] rawArgs) {
        try {
            // setup logging
            var argsParser = new ArgsParser(rawArgs);
            var args = argsParser.parse(RunnerArgs.class);
            AssertArgs.notNull(args, a -> a.run, "no 'Runnable' defined");
            logSettings.setLogLevel(args.logLevel);
            logger().info("Starting 'Runnable' {} ...", args.run);
            logMaxMemory();

            // find classes
            var ru = new ReflectionUtils(InjectorConfig.class, argsParser);
            var modules = ru.loadModules(THIS_PACKAGE);
            for (var basePkg : basePkgs) {
                if (!THIS_PACKAGE.equals(basePkg)) {
                    modules.addAll(ru.loadModules(basePkg));
                }
            }
            var runnableClass = ru.findRunnableClass(args.run);

            // setup injector and run requested plugin
            var injector = Guice.createInjector(modules);
            injector.getInstance(runnableClass).run();
        } catch (Throwable t) {
            if (isAssertArgsError(t)) {
                // silent shutdown, warnings were already printed
                System.exit(1);
            }

            logger().error("Throwable caught in main loader class, shutting down VM ...", t);
            // make sure to tear down VM, including all running threads
            System.exit(1);
        }

    }

    private boolean isAssertArgsError(Throwable t) {
        return t instanceof AssertArgsError
                || t instanceof ProvisionException && t.getCause() instanceof AssertArgsError;
    }

    private static Logger logger() {
        return getLogger(Runner.class);
    }

    private void logMaxMemory() {
        var mega = 1024.0 * 1024.0;
        var mm = Runtime.getRuntime().maxMemory() / mega;
        logger().info("Max memory: {} MB", mm);
    }
}