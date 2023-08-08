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
package example;

import dev.c0ps.diapper.Runner;

public class Main {

    // There are two different example applications that can be started:
    // --run example.c1.Main1 --name John --num 4
    // --run example.c2.Main2 --foo "some string" --num 6
    public static void main(String[] rawArgs) {
        // provide a (list of) package names, in which injector configs will be searched
        // for during class path scanning
        new Runner("example", "other.package", "...").run(rawArgs);

        // the application can be packages into a .jar file with lib folder by invoking
        // "mvn clean package"
    }
}