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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VmArgs {

    private static final Logger LOG = LoggerFactory.getLogger(VmArgs.class);

    private VmArgs() {
        // no instantiation
    }

    public static String[] addRunnable(String[] args, Class<? extends Runnable> c) {
        return prepend(args, "--run", c.getName());
    }

    public static String format(String[] args) {
        return String.join(" ", args);
    }

    public static void log(String[] args) {
        LOG.info("VM Arguments: {}", format(args));
    }

    public static String[] append(String[] args, String... add) {
        var newArgs = new String[args.length + add.length];
        System.arraycopy(args, 0, newArgs, 0, args.length);
        System.arraycopy(add, 0, newArgs, args.length, add.length);
        return newArgs;
    }

    public static String[] prepend(String[] args, String... add) {
        var newArgs = new String[args.length + add.length];
        System.arraycopy(add, 0, newArgs, 0, add.length);
        System.arraycopy(args, 0, newArgs, add.length, args.length);
        return newArgs;
    }
}