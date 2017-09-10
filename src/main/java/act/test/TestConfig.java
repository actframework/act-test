package act.test;

/*-
 * #%L
 * ACT TEST
 * %%
 * Copyright (C) 2015 - 2017 ActFramework
 * %%
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
 * #L%
 */

import act.app.App;
import act.app.conf.AutoConfig;
import act.app.conf.AutoConfigPlugin;
import act.plugin.AppServicePlugin;

/**
 * Defines Test Plugin configurations
 */
@AutoConfig("test")
public class TestConfig extends AppServicePlugin {

    /**
     * Defines the root folder where test scenarios are located.
     * The root folder shall always sit under {@code src/test/resources}
     */
    public static String root = "scenario";

    @Override
    protected void applyTo(App app) {
        AutoConfigPlugin.loadPluginAutoConfig(TestConfig.class, app);
    }
}
