package act.test;

import act.db.morphia.util.JodaDateTimeConverter;
import act.test.util.ActDbTestBase;
import com.github.fakemongo.Fongo;
import org.junit.Before;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public abstract class MongoTestBase extends ActDbTestBase {

    private static final String DB_NAME = "test";

    private Morphia morphia;
    private Datastore ds;

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
