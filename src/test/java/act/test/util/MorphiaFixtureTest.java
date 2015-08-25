package act.test.util;

import act.app.DbServiceManager;
import act.db.Dao;
import act.db.DbService;
import act.test.MongoTestBase;
import act.test.TestBase;
import model.morphia.Account;
import model.morphia.Address;
import model.morphia.Contact;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgl._;
import org.osgl.util.E;

import java.util.List;
import java.util.Map;

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
        when(mockApp.dbServiceManager()).thenReturn(dbServiceManager);
        when(dbServiceManager.dao(any(Class.class))).thenAnswer(new Answer<Dao>() {
            @Override
            public Dao answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] args = invocationOnMock.getArguments();
                Class<?> modelType = _.cast(args[0]);
                if (Account.class.isAssignableFrom(modelType)) {
                    return accDao;
                } else if (Contact.class.isAssignableFrom(modelType)) {
                    return ctctDao;
                }
                return null;
            }
        });
        fixture = new Fixture(mockApp);
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
