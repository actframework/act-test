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

import act.db.Dao;
import act.db.DaoBase;
import act.db.Model;
import act.db.morphia.MorphiaDao;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;

/**
 * Map Model class to corresponding Dao instance
 */
public interface ModelDaoMapper {

    <T extends Dao> T mapFrom(Class<? extends Model> model);

    enum ModelDaoStructure {
        EmbeddedDao() {
            @Override
            public <T extends Dao> T tryFrom(Class<? extends Model> model) {
                String modelClass = model.getName();
                String daoClass = S.builder(modelClass).append("$Dao").toString();
                try {
                    return $.newInstance(daoClass);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        ;
        public abstract <T extends Dao> T tryFrom(Class<? extends Model> model);
    }

    class DefImpl implements ModelDaoMapper {

        public class DefImplMapper {
            private Class<? extends Model> modelClass;

            private DefImplMapper(Class<? extends Model> modelClass) {
                this.modelClass = modelClass;
            }

            public DefImpl to(Dao dao) {
                map.put(modelClass, daoProcessor.apply(dao));
                return DefImpl.this;
            }
        }

        private ModelDaoStructure modelDaoStructure;
        private $.Function<Dao, Dao> daoProcessor;

        private C.Map<Class<? extends Model>, Dao> map = C.newMap();

        public DefImpl($.Function<Dao, Dao> daoProcessor) {
            modelDaoStructure = ModelDaoStructure.EmbeddedDao;
            this.daoProcessor = daoProcessor;
        }

        public DefImplMapper map(Class<? extends Model> modelClass) {
            return new DefImplMapper(modelClass);
        }

        @Override
        public <T extends Dao> T mapFrom(Class<? extends Model> model) {
            T dao = (T) map.get(model);
            if (null != dao) {
                return dao;
            }
            dao = modelDaoStructure.tryFrom(model);
            if (null != dao) {
                map.put(model, dao);
            }
            return null == dao ? (T) new MorphiaDao(model) : dao;
        }
    }
}
