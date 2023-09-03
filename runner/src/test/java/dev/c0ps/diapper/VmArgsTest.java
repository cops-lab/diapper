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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.c0ps.test.TestLoggerUtils;

public class VmArgsTest {

    @Test
    public void log() {
        TestLoggerUtils.clearLog();
        VmArgs.log(args("a", "b"));
        var actual = TestLoggerUtils.getFormattedLogs(VmArgs.class);
        var expected = List.of("INFO VM Arguments: a b");
        assertEquals(expected, actual);
    }

    @Test
    public void prependNothingAtAll() {
        var actual = VmArgs.prepend(args(), args());
        var expected = args();
        assertNotSame(expected, actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void prependNothingNew() {
        var actual = VmArgs.prepend(args("a"), args());
        var expected = args("a");
        assertNotSame(expected, actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void prependNothingBefore() {
        var actual = VmArgs.prepend(args(), args("a"));
        var expected = args("a");
        assertNotSame(expected, actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void prependMultiple() {
        var actual = VmArgs.prepend(args("a"), args("b", "c"));
        var expected = args("b", "c", "a");
        assertNotSame(expected, actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void appendNothingAtAll() {
        var actual = VmArgs.append(args(), args());
        var expected = args();
        assertNotSame(expected, actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void appendNothingNew() {
        var actual = VmArgs.append(args("a"), args());
        var expected = args("a");
        assertNotSame(expected, actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void appendNothingBefore() {
        var actual = VmArgs.append(args(), args("a"));
        var expected = args("a");
        assertNotSame(expected, actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void appendMultiple() {
        var actual = VmArgs.append(args("a"), args("b", "c"));
        var expected = args("a", "b", "c");
        assertNotSame(expected, actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void addRunnable() {
        var actual = VmArgs.addRunnable(args(), String.class);
        var expected = args("--run", String.class.getName());
        assertNotSame(expected, actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void addRunnablePrepends() {
        var actual = VmArgs.addRunnable(args("a"), String.class);
        var expected = args("--run", String.class.getName(), "a");
        assertNotSame(expected, actual);
        assertArrayEquals(expected, actual);
    }

    private static String[] args(String... args) {
        return args;
    }
}