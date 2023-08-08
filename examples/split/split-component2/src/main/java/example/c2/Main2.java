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
package example.c2;

import example.c1.utils.NumberRepeater;
import jakarta.inject.Inject;

public class Main2 implements Runnable {

    private Args2 args;
    private NumberRepeater num;

    // when annotated with @Inject, components can be requested
    @Inject
    public Main2(Args2 args, NumberRepeater num) {
        this.args = args;

        // Note: NumberRepeater is bound in InjectorConfig of other project!
        this.num = num;
    }

    @Override
    public void run() {
        System.out.printf("Value of --foo is '%s'\n", args.foo);
        System.out.printf("The repeated number is %s.\n", num.get());
    }
}