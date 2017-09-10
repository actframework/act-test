package act.test.util;

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
import act.app.DbServiceManager;
import org.osgl.util.C;
import org.osgl.util.E;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Fixture {

    private DbServiceManager dbServiceManager;
    private YamlLoader yamlLoader;

    @Inject
    public Fixture(App app) {
        dbServiceManager = app.dbServiceManager();
        yamlLoader = new YamlLoader();
    }

    public Map<String, Object> loadYamlFile(String yamlFile) {
        return loadYamlFile(yamlFile, "model");
    }

    public Map<String, Object> loadYamlFile(URL url, String pkgName) {
        return yamlLoader.load(url, pkgName, dbServiceManager);
    }

    public Map<String, Object> loadYamlFile(String yamlFile, String pkgName) {
        File file = new File(yamlFile);
        if (!file.exists() && !file.canRead()) {
            URL url = getClass().getResource(yamlFile);
            if (null == url) {
                throw E.unexpected("Cannot find yaml file: %s", yamlFile);
            } else {
                return yamlLoader.load(url, pkgName, dbServiceManager);
            }
        } else {
            return yamlLoader.load(file, pkgName, dbServiceManager);
        }
    }

}
