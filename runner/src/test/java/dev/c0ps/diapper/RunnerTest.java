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
package dev.c0ps.diapper;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static dev.c0ps.test.TestLoggerUtils.getFormattedLogs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import com.google.inject.Provides;

import dev.c0ps.test.TestLoggerUtils;
import other.OtherConfig;
import other2.YetAnotherConfig;

public class RunnerTest {

    private static final String BASE_PKG = RunnerTest.class.getPackageName();

    private Runner sut;

    @BeforeEach
    public void setup() {
        sut = new Runner(BASE_PKG);

        TestPlugin.wasCalled = false;
        TestLoggerUtils.clearLog();
    }

    @Test
    public void pluginCanBeStarted() {
        assertFalse(TestPlugin.wasCalled);
        sut.run(new String[] { "--run", TestPlugin.class.getName() });
        assertTrue(TestPlugin.wasCalled);
    }

    @Test
    public void providedPackagesAreLookedUp() {
        sut = new Runner( //
                OtherConfig.class.getPackageName(), //
                YetAnotherConfig.class.getPackageName());
        sut.run(new String[] { "--run", MultiConfigPlugin.class.getName() });
        // test is successful when runnable can be provisioned
    }

    @Test
    public void logSettingDefaultIsSet() {
        var logSettings = mock(ILogSettings.class);
        sut = new Runner(logSettings, BASE_PKG);
        sut.run(new String[] { "--run", TestPlugin.class.getName() });
        verify(logSettings).setLogLevel(LogLevel.INFO);
    }

    @Test
    public void logSettingCanBeChanged() {
        var logSettings = mock(ILogSettings.class);
        sut = new Runner(logSettings, BASE_PKG);
        sut.run(new String[] { "--run", TestPlugin.class.getName(), "--logLevel", "ERROR" });
        verify(logSettings).setLogLevel(LogLevel.ERROR);
    }

    @Test
    public void basicInfoReported() {
        sut.run(new String[] { "--run", TestPlugin.class.getName() });
        var logs = getFormattedLogs(Runner.class);
        assertEquals(2, logs.size());
        var msg = String.format("INFO Starting 'Runnable' %s ...", TestPlugin.class.getName());
        assertTrue(logs.contains(msg));
        assertTrue(logs.get(1).startsWith("INFO Max memory: "));
    }

    @Test
    public void missingPluginPrintsUsage() throws Exception {
        var out = SystemLambda.tapSystemOut(() -> {
            catchSystemExit(() -> {
                sut.run(new String[] {});
            });
        });
        assertTrue(out.contains("Insufficient startup arguments"));
        assertTrue(out.contains("no 'Runnable' defined"));
        assertTrue(out.contains("The *subset* of related arguments"));
        assertTrue(out.contains("--run"));
    }

    @Test
    public void handleThrowables() throws Exception {
        catchSystemExit(() -> {
            sut.run(new String[] { "--run", ThrowingPlugin.class.getName() });
        });
        var logs = getFormattedLogs(Runner.class);
        assertTrue(logs.contains("ERROR Throwable caught in main loader class, shutting down VM ..."));
    }

    @Test
    public void handleAssertArgErrorsRun() throws Exception {
        catchSystemExit(() -> {
            sut.run(new String[] { "--run", ArgsErrorRunPlugin.class.getName() });
        });
        var logs = getFormattedLogs(Runner.class);
        assertEquals(2, logs.size());
    }

    @Test
    public void handleAssertArgsErrorsInit() throws Exception {
        catchSystemExit(() -> {
            sut.run(new String[] { "--run", ArgsErrorInitPlugin.class.getName() });
        });
        var logs = getFormattedLogs(Runner.class);
        assertEquals(2, logs.size());
    }

    @Test
    public void handleProvisionErrorsInit() throws Exception {
        catchSystemExit(() -> {
            sut.run(new String[] { "--run", ProvisionErrorInitPlugin.class.getName() });
        });
        var logs = getFormattedLogs(Runner.class);
        assertEquals(3, logs.size());
        assertTrue(logs.contains("ERROR Throwable caught in main loader class, shutting down VM ..."));
    }

    public static class TestPlugin implements Runnable {

        public static boolean wasCalled = false;

        @Override
        public void run() {
            wasCalled = true;
        }
    }

    public static class MultiConfigPlugin implements Runnable {

        @Inject
        public MultiConfigPlugin( //
                RunnerArgs ra, // bound in built-in module
                ILogSettings ls, // bound in "other" module
                Runnable r // bound in "other2" module
        ) {}

        @Override
        public void run() {}
    }

    public static class ThrowingPlugin implements Runnable {

        @Override
        public void run() {
            throw new RuntimeException("RTE");
        }
    }

    public static class ProvisionErrorInitPlugin implements Runnable {

        @Inject
        public ProvisionErrorInitPlugin(List<String> l) {}

        @Override
        public void run() {}
    }

    @InjectorConfig
    public static class ArgsErrorInitConfig extends InjectorConfigBase {
        @Provides
        public IInjectorConfig provideSomethingButFail() {
            throw new AssertArgsError();
        }
    }

    public static class ArgsErrorInitPlugin implements Runnable {

        @Inject
        public ArgsErrorInitPlugin(IInjectorConfig cfg) {}

        @Override
        public void run() {}
    }

    public static class ArgsErrorRunPlugin implements Runnable {

        @Override
        public void run() {
            throw new AssertArgsError();
        }
    }
}