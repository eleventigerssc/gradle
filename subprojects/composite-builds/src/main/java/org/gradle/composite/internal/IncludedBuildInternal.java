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

package org.gradle.composite.internal;

import org.gradle.BuildResult;
import org.gradle.includedbuild.ConfigurableIncludedBuild;
import org.gradle.api.internal.GradleInternal;
import org.gradle.api.internal.SettingsInternal;
import org.gradle.api.internal.artifacts.ivyservice.dependencysubstitution.DependencySubstitutionsInternal;

public interface IncludedBuildInternal extends ConfigurableIncludedBuild {
    DependencySubstitutionsInternal resolveDependencySubstitutions();
    SettingsInternal getLoadedSettings();
    GradleInternal getConfiguredBuild();
    BuildResult execute(Iterable<String> tasks, Object listener);
}
