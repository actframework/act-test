package act.test.util;

import act.app.DbServiceManager;
import act.db.Dao;
import act.test.MongoTestBase;
import model.morphia.Account;
import model.morphia.Contact;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgl.$;

import static org.mockito.Mockito.*;

public class MorphiaFixtureTest extends MongoTestBase {

    private DbServiceManager dbServiceManager;
    private Fixture fixture;
    private Account.Dao accDao;
    private Contact.Dao ctctDao;

    @Override
    protected void prepareData() {
        accDao = new Account.Dao();
        accDao.setDatastore(ds());
        ctctDao = new Contact.Dao(accDao);
        ctctDao.setDatastore(ds());
        dbServiceManager = mock(DbServiceManager.class);
        when(app.dbServiceManager()).thenReturn(dbServiceManager);
        when(dbServiceManager.dao(any(Class.class))).thenAnswer(new Answer<Dao>() {
            @Override
            public Dao answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] args = invocationOnMock.getArguments();
                Class<?> modelType = $.cast(args[0]);
                if (Account.class.isAssignableFrom(modelType)) {
                    return accDao;
                } else if (Contact.class.isAssignableFrom(modelType)) {
                    return ctctDao;
                }
                return null;
            }
        });
        fixture = new Fixture(app);
        super.prepareData();
    }

    @Test
    public void testLoad() throws Exception {
        fixture.loadYamlFile("/data-with-reference.yaml");
        Contact tom = ctctDao.findOneBy("firstName", "Tom");
        Account company = ctctDao.getCompany(tom);
        eq("IBM", company.getName());
    }

}
