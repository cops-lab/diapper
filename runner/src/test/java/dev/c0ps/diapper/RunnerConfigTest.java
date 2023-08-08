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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.google.inject.Binder;
import com.google.inject.binder.AnnotatedBindingBuilder;

public class RunnerConfigTest {

    @SuppressWarnings("unchecked")
    @Test
    public void bindArgs() {
        var args = mock(RunnerArgs.class);
        var binder = mock(Binder.class);
        var builder = mock(AnnotatedBindingBuilder.class);
        when(binder.bind(RunnerArgs.class)).thenReturn(builder);

        var sut = new RunnerConfig(args);
        sut.configure(binder);

        verify(binder).bind(RunnerArgs.class);
        verify(builder).toInstance(args);
    }
}