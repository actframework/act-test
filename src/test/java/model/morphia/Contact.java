package model.morphia;

import act.db.morphia.MorphiaDao;
import act.db.morphia.MorphiaModel;
import org.bson.types.ObjectId;
import org.osgl.$;

import javax.inject.Inject;

public class Contact extends MorphiaModel {

    private String firstName;
    private String lastName;
    private Address address;
    private ObjectId companyId;

    private Contact() {}

    public Contact(String fname, String lname) {
        this.firstName = fname;
        this.lastName = lname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ObjectId getCompanyId() {
        return companyId;
    }

    public void setCompanyId(ObjectId companyId) {
        this.companyId = companyId;
    }

    public static class Dao extends MorphiaDao<Contact> {

        private Account.Dao accDao;

        @Inject
        public Dao(Account.Dao accDao) {
            super(Contact.class);
            this.accDao = $.NPE(accDao);
        }

        public Account getCompany(Contact contact) {
            return accDao.findById(contact.getCompanyId());
        }
    }
}
