/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.featurelifecycle;

/**
 * This class is used to test proper call stack evaluation of Java classes.
 */
public class SimulatedJavaCallLocation {

    static DeprecatedFeatureUsage create() {
        return SimulatedDeprecationMessageLogger.nagUserWith(SimulatedDeprecationMessageLogger.DIRECT_CALL);
    }

    static DeprecatedFeatureUsage indirectly() {
        return SimulatedDeprecationMessageLogger.indirectly(SimulatedDeprecationMessageLogger.INDIRECT_CALL);
    }

    static DeprecatedFeatureUsage indirectly2() {
        return SimulatedDeprecationMessageLogger.indirectlySecondLevel(SimulatedDeprecationMessageLogger.INDIRECT_CALL_2);
    }
}