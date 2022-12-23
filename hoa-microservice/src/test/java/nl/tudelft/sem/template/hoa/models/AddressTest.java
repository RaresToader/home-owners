package nl.tudelft.sem.template.hoa.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AddressTest {

    @Test
    void constructorTest() {
        Address address = new Address("Country", "City", "Street", "30A", "2011AD");
        Assertions.assertNotNull(address);
    }

    @Test
    void constructorTestFromDB() {
        Address address = new Address("Country,City,Street,30A,2011AD");
        Address test = new Address("Country", "City", "Street", "30A", "2011AD");
        Assertions.assertNotNull(address);
        Assertions.assertEquals(address, test);
    }

    @Test
    void toDbString() {
        Address test = new Address("Country", "City", "Street", "30A", "2011AD");
        Assertions.assertEquals(test.toDbString(), "Country,City,Street,30A,2011AD");
    }

    @Test
    void toStringTest() {
        Address test = new Address("Country", "City", "Street", "30A", "2011AD");
        String toString = "Address{country='Country', city='City', street='Street', houseNumber='30A', postalCode='2011AD'}";
        Assertions.assertEquals(test.toString(), toString);
    }

    /*
    return "Address{"
                + "country='" + country
                + '\'' + ", city='" + city
                + '\'' + ", street='"
                + street + '\''
                + ", houseNumber='" + houseNumber + '\''
                + ", postalCode='" + postalCode + '\''
                + '}';
     */
}
