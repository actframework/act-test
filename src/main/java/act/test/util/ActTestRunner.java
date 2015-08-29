package act.test.util;

import act.app.App;
import act.test.classloading.ActTestClassLoader;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.osgl._;

/**
 * Allow it to run the test in the context of {@link ActTestClassLoader}
 */
public class ActTestRunner extends Runner {

    private Object innerRunner;
    private Class<? extends Runner> innerRunnerClass;
    private ActTestClassLoader classLoader;

    public ActTestRunner(Class<?> testFileClass) {
        App app = Mockito.mock(App.class);
        String[] pattern = ActClassDiscoverer.getActClassNamePatternsOn(testFileClass);
        classLoader =  new ActTestClassLoader(app, pattern);
        innerRunnerClass = _.classForName(JUnit4.class.getName(), classLoader);
        String testFileClassName = testFileClass.getName();
        Class<?> testClass = _.classForName(testFileClassName, classLoader);
        innerRunner = _.newInstance(innerRunnerClass, testClass);
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
