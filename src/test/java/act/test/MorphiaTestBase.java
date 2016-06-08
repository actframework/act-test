package act.test;

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
    protected DaoBase daoBase() {
        return new MorphiaDao();
    }

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
