package model.morphia;

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

import act.db.morphia.MorphiaDao;
import act.db.morphia.MorphiaModel;
import org.bson.types.ObjectId;
import org.osgl.$;

import javax.inject.Inject;
import java.util.concurrent.ConcurrentMap;

public class Contact extends MorphiaModel<Contact> {

    private String firstName;
    private String lastName;
    private Address address;
    private ObjectId companyId;

    public Contact() {}

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
