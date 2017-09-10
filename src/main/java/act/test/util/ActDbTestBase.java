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
