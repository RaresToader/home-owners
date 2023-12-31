package voting.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import voting.annotations.Generated;

/**
 * A DDD value object representing an Address
 */
@EqualsAndHashCode
@Data
@Generated // Address model checks are done in HOA, hence this microservice assumes a clean/validated model
public class Address {
    private final String country;
    private final String city;
    private final String street;
    private final String houseNumber;
    private final String postalCode;

    /**
     * Creates an Address DTO
     *
     * @param country     Country of address
     * @param city        City of address
     * @param street      Street of address (name only)
     * @param houseNumber House number (including its suffix)
     * @param postalCode  Postal code (postalCode, may include letters)
     */
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
     *
     * @param fromDB DB String to convert from
     */
    public Address(String fromDB) {
        String[] extracted = fromDB.split(",");

        this.country = extracted.length > 0 ? extracted[0] : "";
        this.city = extracted.length > 1 ? extracted[1] : "";
        this.street = extracted.length > 2 ? extracted[2] : "";
        this.houseNumber = extracted.length > 3 ? extracted[3] : "";
        this.postalCode = extracted.length > 4 ? extracted[4] : "";
    }

    @Override
    public String toString() {
        return "Address{"
                + "country='" + country + '\''
                + ", city='" + city + '\''
                + ", street='" + street + '\''
                + ", houseNumber='" + houseNumber + '\''
                + ", postalCode='" + postalCode + '\''
                + '}';
    }

    public String toDBString() {
        return country + "," + city + "," + street + "," + houseNumber + "," + postalCode;
    }
}