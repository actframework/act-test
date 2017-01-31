package act.test.util;

import act.Act;
import act.app.DbServiceManager;
import act.db.Dao;
import act.db.DaoBase;
import act.db.DbManager;
import act.db.Model;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgl.$;
import org.osgl.Osgl;

import java.lang.reflect.Field;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public abstract class ActDbTestBase extends ActTestBase {

    protected DbManager dbManager;
    protected DbServiceManager dbServiceManager;
    protected ModelDaoMapper modelDaoMapper;
    private $.Function<Dao, Dao> daoProcessor;

    protected $.Function<Dao, Dao> createDaoProcessor() {
        return $.F.identity();
    }

    protected ModelDaoMapper createModelDaoMapper() {
        return new ModelDaoMapper.DefImpl(daoProcessor);
    }

    protected void setupModelDaoMapper(ModelDaoMapper modelDaoMapper) {
        // sub class to override
    }

    protected void setup() throws Exception {
        super.setup();
        setupDbManager();
        setDbServiceManager();
        daoProcessor = createDaoProcessor();
        modelDaoMapper = createModelDaoMapper();
        setupModelDaoMapper(modelDaoMapper);
    }

    private void setupDbManager() throws Exception {
        dbManager = new DbManager();
        Field field = Act.class.getDeclaredField("dbManager");
        field.setAccessible(true);
        field.set(null, dbManager);
    }

    private void setDbServiceManager() {
        dbServiceManager = Mockito.mock(DbServiceManager.class);
        when(app.dbServiceManager()).thenReturn(dbServiceManager);
        when(dbServiceManager.dao(any(Class.class))).thenAnswer(new Answer<Dao>() {
            @Override
            public Dao answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] args = invocationOnMock.getArguments();
                Class<? extends Model> modelType = $.cast(args[0]);
                Dao dao = modelDaoMapper.mapFrom(modelType);
                return daoProcessor.apply(dao);
            }
        });
    }
}
