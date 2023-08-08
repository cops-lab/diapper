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
package dev.c0ps.diapper.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.c0ps.diapper.IInjectorConfig;

public class ReflectionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);

    private final Class<? extends Annotation> markerAnnotation;
    private final ArgsParser argsParser;

    public ReflectionUtils(Class<? extends Annotation> markerAnnotation, ArgsParser argsParser) {
        this.markerAnnotation = markerAnnotation;
        this.argsParser = argsParser;
    }

    public Set<IInjectorConfig> loadModules(String... basePkgs) {
        var modules = new HashSet<IInjectorConfig>();
        for (var basePkg : basePkgs) {
            LOG.info("Searching for @{} in package {} ...", markerAnnotation.getSimpleName(), basePkg);
            var ref = new Reflections(basePkg);
            for (Class<?> cl : ref.getTypesAnnotatedWith(markerAnnotation)) {
                var m = loadModule(cl, argsParser);
                if (m != null) {
                    modules.add(m);
                }
            }
        }
        return modules;
    }

    private static IInjectorConfig loadModule(Class<?> cl, ArgsParser args) {
        LOG.info("Loading {} ...", cl.getName());
        if (!IInjectorConfig.class.isAssignableFrom(cl)) {
            LOG.error("Class {} does not implement {}", cl.getName(), IInjectorConfig.class.getName());
            return null;
        }

        try {
            @SuppressWarnings("unchecked")
            var inits = (Constructor<IInjectorConfig>[]) cl.getConstructors();
            if (inits.length > 1) {
                LOG.error("{} should have at most one constructor, but has {}", cl.getName(), inits.length);
                return null;
            }
            var init = (Constructor<IInjectorConfig>) inits[0];

            int parameterCount = init.getParameterCount();
            if (parameterCount == 0) {
                return init.newInstance();
            }

            var argObjs = new Object[parameterCount];
            for (var i = 0; i < parameterCount; i++) {
                var paramType = init.getParameters()[i].getType();
                var argObj = args.parse(paramType);
                argObjs[i] = argObj;
            }
            return init.newInstance(argObjs);

        } catch (InvocationTargetException e) {
            var causingClass = e.getCause().getClass().getName();
            var causingMsg = e.getCause().getMessage();
            LOG.error("Construction of {} failed with {}: {}", cl.getName(), causingClass, causingMsg);
            return null;

        } catch (Exception e) {
            LOG.error("Failed to initialize {} ({})", cl.getName(), e.getClass().getName());
            return null;
        }
    }

    public Class<Runnable> findRunnableClass(String name) {
        try {
            var c = Class.forName(name);
            if (!Runnable.class.isAssignableFrom(c)) {
                LOG.error("Class {} does not implement {}", c.getName(), Runnable.class.getName());
                System.exit(1);
            }
            @SuppressWarnings("unchecked")
            var pc = (Class<Runnable>) c;
            return pc;
        } catch (ClassNotFoundException e) {
            LOG.error("Class cannot be found: {}", name);
            System.exit(1);
        }
        return null;
    }
}