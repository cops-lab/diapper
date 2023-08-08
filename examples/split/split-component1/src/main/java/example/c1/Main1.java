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
package example.c1;

import example.c1.utils.Name;
import example.c1.utils.NumberRepeater;
import jakarta.inject.Inject;

public class Main1 implements Runnable {

    private Args1 args;

    private Name name;
    private NumberRepeater number;

    // when annotated with @Inject, components can be requested
    @Inject
    public Main1(Args1 args, Name foo, NumberRepeater bar) {
        this.args = args;
        this.name = foo;
        this.number = bar;
    }

    @Override
    public void run() {
        System.out.printf("You can directly access arguments like --name: %s\n", args.name);
        System.out.printf("Or injected instances, like %s or %s!", name.getAsUpperCase(), number.get());
    }
}