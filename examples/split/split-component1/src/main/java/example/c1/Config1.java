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

import static dev.c0ps.diapper.AssertArgs.assertFor;

import com.google.inject.Provides;
import com.google.inject.Singleton;

import dev.c0ps.diapper.AssertArgs;
import dev.c0ps.diapper.InjectorConfig;
import dev.c0ps.diapper.InjectorConfigBase;
import example.c1.utils.Name;
import example.c1.utils.NumberRepeater;

@InjectorConfig
public class Config1 extends InjectorConfigBase {

    private Args1 args;

    // to use custom args, just refer to them in the constructor
    public Config1(Args1 args) {
        this.args = args;
    }

    // bind type to instances to make them injectable in components
    @Provides
    @Singleton
    public Args1 bindArgs1() {
        return args;
    }

    @Provides
    @Singleton
    public Name bindFoo() {
        // perform args validation in @Provides, this ensures that errors are only
        // raised, when the parameter is actually required for running the app
        assertFor(args) //
                .notNull(args -> args.name, "must provide a name") //
                .that(args -> args.name.length() >= 3, "name must be at least 3 characters");
        return new Name(args.name);
    }

    // not adding @Singleton causes creation of multiple instances on request
    @Provides
    public NumberRepeater bindBar() {
        // perform args validation in @Provides, this ensures that errors are only
        // raised, when the parameter is actually required for running the app
        AssertArgs.assertFor(args)//
                .notNull(a -> a.num, "must provide a number") //
                .that(a -> a.num > 0, "number must be set to a value greater 0");
        return new NumberRepeater(args.num);
    }

    // All Google Guice options can be used here.
}