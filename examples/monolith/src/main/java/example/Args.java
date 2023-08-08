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

import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(names = "--name", arity = 1, description = "Name to be greeted.")
    public String name;

    // All JCommander options can be used, just DO NOT make parameters "required".
    // Instead, validate parameters in the InjectorConfig, e.g., using AssertArgs.
}