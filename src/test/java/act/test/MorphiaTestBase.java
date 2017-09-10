package act.test;

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

import act.db.Dao;
import act.db.DaoBase;
import act.db.morphia.MorphiaDao;
import act.db.morphia.util.JodaDateTimeConverter;
import act.test.util.ActDbTestBase;
import com.github.fakemongo.Fongo;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.osgl.$;
import org.osgl.Osgl;

public abstract class MorphiaTestBase extends ActDbTestBase {

    private static final String DB_NAME = "test";

    private Morphia morphia;
    private Datastore ds;

    @Override
    protected $.Function<Dao, Dao> createDaoProcessor() {
        return new $.Function<Dao, Dao>() {
            @Override
            public Dao apply(Dao dao) throws Osgl.Break {
                MorphiaDao morphiaDao = $.cast(dao);
                morphiaDao.setDatastore(ds());
                return dao;
            }
        };
    }

    public final void setup() throws Exception {
        super.setup();
        prepareMongoDB();
    }

    public final void prepareMongoDB() {
        morphia = new Morphia();
        mapClasses();
        ds = morphia.createDatastore(new Fongo(DB_NAME).getMongo(), DB_NAME);
        prepareData();
    }

    protected final Morphia morphia() {
        return morphia;
    }
    protected final Datastore ds() {
        return ds;
    }

    protected void prepareData() {}

    protected void mapClasses() {
        morphia.getMapper().getConverters().addConverter(new JodaDateTimeConverter());
        morphia.mapPackage("model.morphia", true);
    }

}
