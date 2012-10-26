/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.batch.bootstrap;

import org.junit.Test;
import org.sonar.api.batch.bootstrap.ProjectDefinition;
import org.sonar.api.batch.bootstrap.ProjectReactor;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.Project;
import org.sonar.batch.FakeMavenPluginExecutor;
import org.sonar.batch.MavenPluginExecutor;

import static org.fest.assertions.Assertions.assertThat;


public class BootstrapModuleTest {

  private ProjectReactor reactor = new ProjectReactor(ProjectDefinition.create());

  @Test
  public void should_register_fake_maven_executor_if_not_maven_env() {
    BootstrapModule module = new BootstrapModule(reactor, null, MyMavenPluginExecutor.class);
    module.init();

    assertThat(module.isMavenPluginExecutorRegistered()).isTrue();
    assertThat(module.container.getComponentByType(MavenPluginExecutor.class)).isInstanceOf(MyMavenPluginExecutor.class);
  }

  @Test
  public void should_use_plugin_executor_provided_by_maven() {
    BootstrapModule module = new BootstrapModule(reactor);
    module.init();
    assertThat(module.isMavenPluginExecutorRegistered()).isFalse();
    assertThat(module.container.getComponentByType(MavenPluginExecutor.class)).isInstanceOf(FakeMavenPluginExecutor.class);
  }

  @Test
  public void should_register_bootstrap_components() {
    BootstrapModule module = new BootstrapModule(reactor, new FakeComponent());
    module.init();

    assertThat(module.container).isNotNull();
    assertThat(module.container.getComponentByType(FakeComponent.class)).isNotNull();
    assertThat(module.container.getComponentByType(ProjectReactor.class)).isSameAs(reactor);
  }

  @Test
  public void should_not_fail_if_no_bootstrap_components() {
    BootstrapModule module = new BootstrapModule(reactor);
    module.init();

    assertThat(module.container).isNotNull();
    assertThat(module.container.getComponentByType(ProjectReactor.class)).isSameAs(reactor);
  }

  public static class FakeComponent {

  }

  public static class MyMavenPluginExecutor implements MavenPluginExecutor {
    public void execute(Project project, ProjectDefinition projectDef, String goal) {
    }

    public MavenPluginHandler execute(Project project, ProjectDefinition projectDef, MavenPluginHandler handler) {
      return handler;
    }
  }
}
