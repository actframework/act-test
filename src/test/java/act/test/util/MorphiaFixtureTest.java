package act.test.util;

import act.test.MorphiaTestBase;
import model.morphia.Account;
import model.morphia.Contact;
import org.junit.Test;
import org.osgl.$;

public class MorphiaFixtureTest extends MorphiaTestBase {

    private Fixture fixture;

    @Override
    protected void prepareData() {
        fixture = new Fixture(app);
        super.prepareData();
    }

    @Override
    protected void setupModelDaoMapper(ModelDaoMapper modelDaoMapper) {
        super.setupModelDaoMapper(modelDaoMapper);
        ModelDaoMapper.DefImpl mapper = $.cast(modelDaoMapper);
        Account.Dao accDao = new Account.Dao();
        Contact.Dao ctctDao = new Contact.Dao(accDao);
        mapper
                .map(Account.class).to(accDao)
                .map(Contact.class).to(ctctDao);
    }

    @Test
    public void testLoad() throws Exception {
        fixture.loadYamlFile("/data-with-reference.yaml");
        Contact.Dao contactDao = modelDaoMapper.mapFrom(Contact.class);
        Contact tom = contactDao.findOneBy("firstName", "Tom");
        Account company = contactDao.getCompany(tom);
        eq("IBM", company.getName());
    }

}
