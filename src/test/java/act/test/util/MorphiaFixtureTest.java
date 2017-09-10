package act.test.util;

/*-
 * #%L
 * ACT TEST
 * %%
 * Copyright (C) 2015 - 2017 ActFramework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
