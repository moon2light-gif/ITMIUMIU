package model;

import java.util.Objects;

public class Address {
    private String street; //Поле не может быть null

    public Address(String street) {
        this.street = street;
    }

    public Address() {
        //для Jackson
    }

    public String getStreet() {
        return street;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Address address = (Address) other;
        return Objects.equals(this.street, address.street);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street);
    }

    @Override
    public String toString() {
        return street;
    }
}
