package act.test.util;

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
