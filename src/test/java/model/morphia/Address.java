package model.morphia;

import act.db.morphia.MorphiaModel;

public class Address {
    private String unit;
    private String street;
    private String suburb;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (unit != null ? !unit.equals(address.unit) : address.unit != null) return false;
        if (street != null ? !street.equals(address.street) : address.street != null) return false;
        return !(suburb != null ? !suburb.equals(address.suburb) : address.suburb != null);

    }

    @Override
    public int hashCode() {
        int result = unit != null ? unit.hashCode() : 0;
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (suburb != null ? suburb.hashCode() : 0);
        return result;
    }
}
