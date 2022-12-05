package voting.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;

/**
 * A DDD value object representing an Address
 */
@EqualsAndHashCode
@Data
public class Address {
    private final transient String country;
    private final transient String city;
    private final transient String street;
    private final transient String houseNumber;
    private final transient String postalCode;

    public Address(String country, String city, String street, String houseNumber, String postalCode) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
    }

    /**
     * Creates an Address object from database String
     * Extracts the fields from the toString method
     * @param fromDB DB String to convert from
     */
    public Address(String fromDB) {
        String[] extracted = fromDB.split(",");

        this.country = extracted[0];
        this.city = extracted[1];
        this.street = extracted[2];
        this.houseNumber = extracted[3];
        this.postalCode = extracted[4];
    }

    @Override
    public String toString() {
        return "Address{" +
                "country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }

    public String toDBString() {
        return country + "," + city + "," + street + "," + houseNumber + "," + postalCode;
    }
}