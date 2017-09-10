package act.test.util;

import act.app.App;
import act.job.AppJobManager;
import act.test.classloading.ActTestClassLoader;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.osgl.$;
import org.osgl.util.E;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Allow it to run the test in the context of {@link ActTestClassLoader}
 */
public class ActTestRunner extends Runner {

    private Object innerRunner;
    private Class<? extends Runner> innerRunnerClass;
    private ActTestClassLoader classLoader;

    public ActTestRunner(Class<?> testFileClass) {
        AppJobManager jobManager = Mockito.mock(AppJobManager.class);
        App app = Mockito.mock(App.class);
        Mockito.when(app.jobManager()).thenReturn(jobManager);
        List<String> pattern = ActClassDiscoverer.getActClassNamePatternsOn(testFileClass);
        classLoader =  new ActTestClassLoader(app, pattern);
        URL url = getClass().getResource("/act-test.properties");
        if (null != url) {
            Properties p = new Properties();
            try {
                p.load(url.openStream());
            } catch (IOException e) {
                throw E.ioException(e);
            }
            classLoader.testProperties(p);
        }
        // When it runs (in intellij IDEA) multiple test cases that extends ActTestBase
        // multiple ActTestClassLoader will be created and the
        // last will be come the current thread's context class loader
        // which will trigger strange error when deserialize data from
        // mongodb. Thus we need to comment out this line
        // Thread.currentThread().setContextClassLoader(classLoader);
        innerRunnerClass = $.classForName(JUnit4.class.getName(), classLoader);
        String testFileClassName = testFileClass.getName();
        Class<?> testClass = $.classForName(testFileClassName, classLoader);
        innerRunner = $.newInstance(innerRunnerClass, testClass);
    }

    @Override
    public Description getDescription() {
        try {
            return (Description) innerRunnerClass.getMethod("getDescription").invoke(innerRunner);
        } catch (Exception e) {
            throw new RuntimeException("Could not get description", e);
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            innerRunnerClass.getMethod("run", RunNotifier.class).invoke(innerRunner, notifier);
        } catch (Exception e) {
            notifier.fireTestFailure(new Failure(getDescription(), e));
        }
    }
}
