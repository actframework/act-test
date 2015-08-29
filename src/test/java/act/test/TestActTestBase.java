package act.test;

import act.test.util.ActTestBase;
import org.junit.Test;

public class TestActTestBase extends ActTestBase {

    @Test
    public void testA() {
        actionContext.attribute("foo", "bar");
        eq("bar", actionContext.attribute("foo"));
    }

}
