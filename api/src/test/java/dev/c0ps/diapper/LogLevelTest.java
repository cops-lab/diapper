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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.logging.Level;

import org.junit.jupiter.api.Test;

public class LogLevelTest {

    @Test
    public void mappingForAllSlf4j() {
        assertEquals("trace", LogLevel.ALL.slf4j);
    }

    @Test
    public void mappingForDebugSlf4j() {
        assertEquals("debug", LogLevel.DEBUG.slf4j);
    }

    @Test
    public void mappingForErrorSlf4j() {
        assertEquals("error", LogLevel.ERROR.slf4j);
    }

    @Test
    public void mappingForInfoSlf4j() {
        assertEquals("info", LogLevel.INFO.slf4j);
    }

    @Test
    public void mappingForOffSlf4j() {
        assertEquals("off", LogLevel.OFF.slf4j);
    }

    @Test
    public void mappingForWarnSlf4j() {
        assertEquals("warn", LogLevel.WARN.slf4j);
    }

    @Test
    public void mappingForAllJul() {
        assertEquals(Level.ALL, LogLevel.ALL.jul);
    }

    @Test
    public void mappingForDebugJul() {
        assertEquals(Level.FINER, LogLevel.DEBUG.jul);
    }

    @Test
    public void mappingForErrorJul() {
        assertEquals(Level.SEVERE, LogLevel.ERROR.jul);
    }

    @Test
    public void mappingForInfoJul() {
        assertEquals(Level.CONFIG, LogLevel.INFO.jul);
    }

    @Test
    public void mappingForOffJul() {
        assertEquals(Level.OFF, LogLevel.OFF.jul);
    }

    @Test
    public void mappingForWarnJul() {
        assertEquals(Level.WARNING, LogLevel.WARN.jul);
    }
}