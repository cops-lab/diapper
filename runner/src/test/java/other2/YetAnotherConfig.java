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
package other2;

import com.google.inject.Binder;

import dev.c0ps.diapper.IInjectorConfig;
import dev.c0ps.diapper.InjectorConfig;

@InjectorConfig
public class YetAnotherConfig implements IInjectorConfig {

    @Override
    public void configure(Binder binder) {
        binder.bind(Runnable.class).toInstance(new MyRunnable());
    }

    private static class MyRunnable implements Runnable {

        @Override
        public void run() {}
    }
}