package act.test;

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
