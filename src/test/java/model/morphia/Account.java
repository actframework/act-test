package model.morphia;

import act.db.morphia.MorphiaDao;
import act.db.morphia.MorphiaModel;
import org.bson.types.ObjectId;

public class Account extends MorphiaModel {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Dao extends MorphiaDao<ObjectId, Account, Dao> {

        public Dao() {
            super(Account.class);
        }

    }
}
