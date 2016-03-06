package act.test.util;

import act.Act;
import act.db.DbManager;
import org.osgl.mvc.annotation.Before;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class ActDbTestBase extends ActTestBase {

    protected DbManager dbManager;

    @Override
    protected void setup() throws Exception {
        super.setup();
        setupDbManager();
    }

    private void setupDbManager() throws Exception {
        dbManager = new DbManager();
        Field field = Act.class.getDeclaredField("dbManager");
        field.setAccessible(true);
        field.set(null, dbManager);
    }
}
