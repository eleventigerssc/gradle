/*
 * Copyright 2010 the original author or authors.
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

package org.gradle.integtests.samples

import org.gradle.integtests.fixtures.executer.GradleDistribution
import org.gradle.integtests.fixtures.executer.GradleDistributionExecuter
import org.gradle.integtests.fixtures.JUnitTestExecutionResult
import org.gradle.integtests.fixtures.Sample
import org.gradle.util.TestFile
import org.junit.Rule
import org.junit.Test

/**
 * @author Hans Dockter
 */
class SamplesJavaProjectWithIntTestsIntegrationTest {
    @Rule public final GradleDistribution dist = new GradleDistribution()
    @Rule public final GradleDistributionExecuter executer = new GradleDistributionExecuter()
    @Rule public final Sample sample = new Sample('java/withIntegrationTests')

    @Test
    public void canRunIntegrationTests() {
        TestFile javaprojectDir = sample.dir
        
        // Run int tests
        executer.inDirectory(javaprojectDir).withTasks('clean', 'integrationTest').run()

        // Check tests have run
        JUnitTestExecutionResult result = new JUnitTestExecutionResult(javaprojectDir)
        result.assertTestClassesExecuted('org.gradle.PersonIntegrationTest')
    }
}
