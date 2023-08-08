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

import static dev.c0ps.diapper.AssertArgs.assertFor;

import com.google.inject.Provides;
import com.google.inject.Singleton;

import dev.c0ps.diapper.InjectorConfig;
import dev.c0ps.diapper.InjectorConfigBase;

@InjectorConfig
public class Config2 extends InjectorConfigBase {

    private Args2 args;

    // to use custom args, just refer to them in the constructor
    public Config2(Args2 args) {
        this.args = args;
    }

    // bind type to instances to make them injectable in components
    @Provides
    @Singleton
    public Args2 bindArgs2() {
        // perform args validation in @Provides, this ensures that errors are only
        // raised, when the parameter is actually required for running the app
        assertFor(args) //
                .notNull(a -> a.foo, "foo must not be null");
        return args;
    }

    // All Google Guice options can be used here.
}