package common.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс адреса, который используется в Organization
 * @author mikhailkramar
 */
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    private String street; //Поле не может быть null

    public Address(String street) {
        this.street = street;
    }

    public Address() {
        //для Jackson
    }

    /**
     * Метод для получения улицы
     * @return street единственное поле Address
     */
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
