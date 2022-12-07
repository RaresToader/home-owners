package nl.tudelft.sem.template.authmember.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * A DDD value object representing an Address.
 */
@EqualsAndHashCode
@Data
public class Address {
    private final transient String country;
    private final transient String city;
    private final transient String street;
    private final transient String houseNumber;
    private final transient String postalCode;

    /**
     * Constructor for the Address value object.
     *
     * @param country     the country
     * @param city        the city
     * @param street      the street
     * @param houseNumber the number of the house
     * @param postalCode  the postal code
     */
    public Address(String country, String city, String street, String houseNumber, String postalCode) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
    }

    /**
     * Creates an Address object from database String.
     * Extracts the fields from the toString method.
     *
     * @param fromDb the string in the database format
     */
    public Address(String fromDb) {
        String[] extracted = fromDb.split(",");

        this.country = extracted[0];
        this.city = extracted[1];
        this.street = extracted[2];
        this.houseNumber = extracted[3];
        this.postalCode = extracted[4];
    }

    @Override
    public String toString() {
        return "Address{"
                + "country='" + country
                + '\'' + ", city='" + city
                + '\'' + ", street='"
                + street + '\''
                + ", houseNumber='" + houseNumber + '\''
                + ", postalCode='" + postalCode + '\''
                + '}';
    }

    public String toDbString() {
        return country + "," + city + "," + street + "," + houseNumber + "," + postalCode;
    }
}