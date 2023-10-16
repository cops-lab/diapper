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

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static dev.c0ps.diapper.AssertArgs.TEXT_ERROR_INTRO;
import static dev.c0ps.diapper.AssertArgs.TEXT_GENERIC_ERROR;
import static dev.c0ps.diapper.AssertArgs.TEXT_INTRO_FOR_PARAMS;
import static dev.c0ps.diapper.AssertArgs.TEXT_IS_NULL_ERROR;
import static dev.c0ps.diapper.AssertArgs.TEXT_STRING_NULL_OR_EMPTY;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.security.InvalidParameterException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.beust.jcommander.Parameter;
import com.github.stefanbirkner.systemlambda.Statement;
import com.github.stefanbirkner.systemlambda.SystemLambda;

public class AssertArgsTest {

    @TempDir
    private File tempDir;

    private static final String SOME_HINT = "a hint";
    private Args args;

    @BeforeEach
    public void setup() {
        args = new Args();
    }

    @Test
    public void assertThatFail() throws Exception {
        assertErrorOutput(() -> {
            AssertArgs.that(new Args(), o -> false, SOME_HINT);
        }, new String[] { //
                TEXT_ERROR_INTRO, //
                TEXT_GENERIC_ERROR, //
                SOME_HINT, //
                TEXT_INTRO_FOR_PARAMS, //
                "-x", //
                "Default: 3" });
    }

    @Test
    public void assertThatOk() throws Exception {
        assertOutput(() -> {
            AssertArgs.that(new Args(), o -> true, SOME_HINT);
        });
    }

    @Test
    public void assertNonNullFail() throws Exception {
        assertErrorOutput(() -> {
            AssertArgs.notNull(new Args(), o -> null, SOME_HINT);
        }, new String[] { //
                TEXT_ERROR_INTRO, //
                TEXT_IS_NULL_ERROR, //
                SOME_HINT, //
                TEXT_INTRO_FOR_PARAMS, //
                "-x", //
                "Default: 3" });
    }

    @Test
    public void assertStringNullOrEmpty_ok() throws Exception {
        SystemLambda.tapSystemOut(() -> {
            AssertArgs.notNullAndNotEmpty(new Args(), o -> "...", SOME_HINT);
        });
    }

    @Test
    public void assertStringNullOrEmpty_null() throws Exception {
        assertErrorOutput(() -> {
            AssertArgs.notNullAndNotEmpty(new Args(), o -> null, SOME_HINT);
        }, new String[] { //
                TEXT_ERROR_INTRO, //
                TEXT_STRING_NULL_OR_EMPTY, //
                SOME_HINT, //
                TEXT_INTRO_FOR_PARAMS, //
                "-x", //
                "Default: 3" });
    }

    @Test
    public void assertStringNullOrEmpty_empty() throws Exception {
        assertErrorOutput(() -> {
            AssertArgs.notNullAndNotEmpty(new Args(), o -> "", SOME_HINT);
        }, new String[] { //
                TEXT_ERROR_INTRO, //
                TEXT_STRING_NULL_OR_EMPTY, //
                SOME_HINT, //
                TEXT_INTRO_FOR_PARAMS, //
                "-x", //
                "Default: 3" });
    }

    @Test
    public void assertDirectoryExists_null() throws Exception {
        assertErrorOutput(() -> {
            AssertArgs.directoryExists(new Args(), o -> null, SOME_HINT);
        }, new String[] { //
                TEXT_ERROR_INTRO, //
                AssertArgs.TEXT_FILE_NULL_NON_EXISTING, //
                SOME_HINT, //
                TEXT_INTRO_FOR_PARAMS, //
                "-x", //
                "Default: 3" });
    }

    @Test
    public void assertDirectoryExists_nonExisting() throws Exception {
        var f = new File(tempDir, "does-not-exist");
        assertErrorOutput(() -> {
            AssertArgs.directoryExists(new Args(), o -> f, SOME_HINT);
        }, new String[] { //
                TEXT_ERROR_INTRO, //
                AssertArgs.TEXT_FILE_NULL_NON_EXISTING, //
                SOME_HINT, //
                TEXT_INTRO_FOR_PARAMS, //
                "-x", //
                "Default: 3" });
    }

    @Test
    public void assertDirectoryExists_noDirectory() throws Exception {
        var f = new File(tempDir, "does-not-exist");
        f.createNewFile();
        assertErrorOutput(() -> {
            AssertArgs.directoryExists(new Args(), o -> f, SOME_HINT);
        }, new String[] { //
                TEXT_ERROR_INTRO, //
                AssertArgs.TEXT_FILE_NO_DIR, //
                SOME_HINT, //
                TEXT_INTRO_FOR_PARAMS, //
                "-x", //
                "Default: 3" });
    }

    @Test
    public void assertDirectoryExists_ok() throws Exception {
        var f = new File(tempDir, "does-not-exist");
        f.mkdirs();
        assertOutput(() -> {
            AssertArgs.directoryExists(new Args(), o -> f, SOME_HINT);
        });
    }

    @Test
    public void assertNonNullOk() throws Exception {
        SystemLambda.tapSystemOut(() -> {
            AssertArgs.notNull(new Args(), o -> new Object(), SOME_HINT);
        });
    }

    @Test
    public void nothingHappensForPassingCases() {
        AssertArgs.that(args, o -> true, SOME_HINT);
    }

    @Test
    public void argObjMustBeInstantiable() {
        assertThrows(InvalidParameterException.class, () -> {
            AssertArgs.that(new NonInstantiable(0), o -> false, SOME_HINT);
        });
    }

    @Test
    public void noExceptionForNonJCommanderStructures() throws Exception {
        tapSystemOut(() -> {
            assertThrows(AssertArgsError.class, () -> {
                AssertArgs.assertFor(new NoArgs()).that(o -> false, SOME_HINT);
            });
        });
    }

    private static void assertOutput(Statement s, String... expecteds) {
        final String[] out = new String[1];
        try {
            out[0] = tapSystemOut(() -> {
                s.execute();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (var expected : expecteds) {
            assertTrue(out[0].contains(expected));
        }
    }

    private static void assertErrorOutput(Statement s, String... expecteds) {
        assertOutput(() -> {
            var wasCalled = false;
            try {
                s.execute();
            } catch (AssertArgsError e) {
                wasCalled = true;
            }
            assertTrue(wasCalled);
        }, expecteds);
    }

    public static class Args {
        @Parameter(names = "-x", arity = 1)
        public int x = 3;
    }

    public static class NoArgs {}

    public static class NonInstantiable {
        public NonInstantiable(int i) {}
    }
}