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
package example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogSomething implements Runnable {

    private static final Logger SLF = LoggerFactory.getLogger(LogSomething.class);
    private static final java.util.logging.Logger JUL = java.util.logging.Logger
            .getLogger(LogSomething.class.getName());

    @Override
    public void run() {
        SLF.info("SLF-info");
        SLF.debug("SLF-debug");
        SLF.error("SLF-error");
        SLF.warn("SLF-warn");
        SLF.trace("SLF-trace");

        JUL.config("JUL-config");
        JUL.fine("JUL-fine");
        JUL.finer("JUL-finer");
        JUL.finest("JUL-finest");
        JUL.warning("JUL-warning");
        JUL.info("JUL-info");
        JUL.severe("JUL-severe");
    }
}