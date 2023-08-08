/*
 * Copyright 2021 Delft University of Technology
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
package dev.c0ps.diapper.utils;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static dev.c0ps.test.TestLoggerUtils.assertLogsContain;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.c0ps.diapper.IInjectorConfig;
import dev.c0ps.diapper.InjectorConfigBase;
import dev.c0ps.diapper.utils.ReflectionUtilsTest.M5.Args;
import dev.c0ps.diapper.utils.ReflectionUtilsTest.M6.Args1;
import dev.c0ps.diapper.utils.ReflectionUtilsTest.M6.Args2;
import dev.c0ps.test.TestLoggerUtils;

public class ReflectionUtilsTest {

    private static final String BASE_PKG = ReflectionUtils.class.getPackageName();

    private ArgsParser argsParser;
    private ReflectionUtils sut;

    @BeforeEach
    public void setup() {
        TestLoggerUtils.clearLog();
        argsParser = mock(ArgsParser.class);
        when(argsParser.parse(Args.class)).thenReturn(new Args());
        when(argsParser.parse(Args1.class)).thenReturn(new Args1());
        when(argsParser.parse(Args2.class)).thenReturn(new Args2());
        sut = new ReflectionUtils(NonExisting.class, argsParser);
    }

    public Set<IInjectorConfig> loadModules(Class<? extends Annotation> a) {
        return new ReflectionUtils(a, argsParser).loadModules(BASE_PKG);
    }

    @Test
    public void findPlugin() {
        var expected = ExamplePlugin.class;
        var actual = sut.findRunnableClass(expected.getName());
        assertEquals(expected, actual);
    }

    @Test
    public void findNonRunnable() throws Exception {
        catchSystemExit(() -> {
            sut.findRunnableClass(String.class.getName());
        });
        assertLogsContain(ReflectionUtils.class, //
                "ERROR Class %s does not implement %s", //
                String.class.getName(), //
                Runnable.class.getName());
    }

    @Test
    public void findNonExisting() throws Exception {
        String nonExisting = "some.non.existing.Class";
        catchSystemExit(() -> {
            sut.findRunnableClass(nonExisting);
        });
        assertLogsContain(ReflectionUtils.class, //
                "ERROR Class cannot be found: %s", //
                nonExisting);
    }

    @Test
    public void notFindingModulesDoesNotCrash() throws Exception {
        assertEmptySet(loadModules(NonExisting.class));
        assertLogs( //
                format("INFO Searching for @%s in package %s ...", //
                        NonExisting.class.getSimpleName(), //
                        ReflectionUtilsTest.class.getPackageName()));
    }

    @Test
    public void moduleDoesNotImplementInterface() throws Exception {
        assertEmptySet(loadModules(DoesNotImplement.class));
        assertLogs( //
                format("INFO Searching for @%s in package %s ...", //
                        DoesNotImplement.class.getSimpleName(), //
                        ReflectionUtilsTest.class.getPackageName()), //
                format("INFO Loading %s ...", M1.class.getName()), //
                format("ERROR Class %s does not implement %s", M1.class.getName(), IInjectorConfig.class.getName()));
    }

    @Test
    public void moduleHasTooManyConstructors() throws Exception {
        assertEmptySet(loadModules(TooManyConstructors.class));
        TestLoggerUtils.assertLogsContain(ReflectionUtils.class, //
                "ERROR %s should have at most one constructor, but has 2", //
                M2.class.getName());
    }

    @Test
    public void moduleWithNoArgsConstructorWorks() throws Exception {
        var actual = loadModules(OnlyNoArgsConstructor.class);
        assertTrue(actual.size() == 1);
        assertTrue(actual.iterator().next() instanceof M3);
    }

    @Test
    public void moduleInitFailsException() throws Exception {
        assertEmptySet(loadModules(InitCrashes.class));
        TestLoggerUtils.assertLogsContain(ReflectionUtils.class, //
                "ERROR Construction of %s failed with %s: x", //
                M4.class.getName(), //
                RuntimeException.class.getName());
    }

    @Test
    public void moduleFullExampleOneArg() throws Exception {
        var mods = loadModules(FullExampleOneArg.class);
        assertTrue(mods.size() == 1);
        var actual = (M5) mods.iterator().next();
        assertNotNull(actual.args);
    }

    @Test
    public void moduleFullExampleTwoArgs() throws Exception {
        var mods = loadModules(FullExampleTwoArgs.class);
        assertTrue(mods.size() == 1);
        var actual = (M6) mods.iterator().next();
        assertNotNull(actual.args1);
        assertNotNull(actual.args2);
    }

    private static void assertEmptySet(Set<?> actual) {
        var expected = Set.of();
        assertEquals(expected, actual);
    }

    private static void assertLogs(String... msgs) {
        for (var msg : msgs) {
            TestLoggerUtils.assertLogsContain(ReflectionUtils.class, msg);
        }
    }

    // test classes and annotations

    public static class ExamplePlugin implements Runnable {
        @Override
        public void run() {}
    }

    @interface NonExisting {}

    @interface DoesNotImplement {}

    @DoesNotImplement
    public static class M1 {}

    @interface TooManyConstructors {}

    @TooManyConstructors
    public static class M2 extends InjectorConfigBase {
        public M2() {}

        public M2(int i) {}
    }

    @interface OnlyNoArgsConstructor {}

    @OnlyNoArgsConstructor
    public static class M3 extends InjectorConfigBase {
        public M3() {}
    }

    @interface InitCrashes {}

    @InitCrashes
    public static class M4 extends InjectorConfigBase {
        public M4() {
            throw new RuntimeException("x");
        }
    }

    @interface FullExampleOneArg {}

    @FullExampleOneArg
    public static class M5 extends InjectorConfigBase {
        public Args args;

        public M5(Args args) {
            this.args = args;
        }

        public static class Args {}
    }

    @interface FullExampleTwoArgs {}

    @FullExampleTwoArgs
    public static class M6 extends InjectorConfigBase {
        public Args1 args1;
        public Args2 args2;

        public M6(Args1 args1, Args2 args2) {
            this.args1 = args1;
            this.args2 = args2;
        }

        public static class Args1 {}

        public static class Args2 {}
    }
}