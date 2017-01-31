package act.test.util;

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
