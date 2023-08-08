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
package other;

import com.google.inject.Binder;

import dev.c0ps.diapper.IInjectorConfig;
import dev.c0ps.diapper.ILogSettings;
import dev.c0ps.diapper.InjectorConfig;
import dev.c0ps.diapper.LogLevel;

@InjectorConfig
public class OtherConfig implements IInjectorConfig {

    @Override
    public void configure(Binder binder) {
        binder.bind(ILogSettings.class).toInstance(new OtherLogSettings());
    }

    private static class OtherLogSettings implements ILogSettings {
        @Override
        public void setLogLevel(LogLevel level) {}
    }
}