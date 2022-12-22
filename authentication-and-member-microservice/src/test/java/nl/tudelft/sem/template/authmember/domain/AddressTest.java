package nl.tudelft.sem.template.authmember.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressTest {

    private transient Address address = new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA");

    @Test
    void testToString() {
        assertEquals("Address{country='Netherlands', city='Delft', "
                + "street='Drebelweg', houseNumber='14', postalCode='1111AA'}", address.toString());
    }

    @Test
    void toDbString() {
        assertEquals("Netherlands,Delft,Drebelweg,14,1111AA", address.toDbString());
    }

    @Test
    void getCountry() {
        assertEquals("Netherlands", address.getCountry());
    }

    @Test
    void getCity() {
        assertEquals("Delft", address.getCity());
    }

    @Test
    void getStreet() {
        assertEquals("Drebelweg", address.getStreet());
    }

    @Test
    void getHouseNumber() {
        assertEquals("14", address.getHouseNumber());
    }

    @Test
    void getPostalCode() {
        assertEquals("1111AA", address.getPostalCode());
    }

    @Test
    void testEquals() {
        Address beta = new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA");
        assertTrue(address.equals(beta));
    }
}