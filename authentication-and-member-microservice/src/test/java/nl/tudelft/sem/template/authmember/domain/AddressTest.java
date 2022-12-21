package nl.tudelft.sem.template.authmember.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    private Address a = new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA");

    @Test
    void testToString() {
        assertEquals("Address{country='Netherlands', city='Delft', street='Drebelweg', houseNumber='14', postalCode='1111AA'}", a.toString());
    }

    @Test
    void toDbString() {
        assertEquals("Netherlands,Delft,Drebelweg,14,1111AA", a.toDbString());
    }

    @Test
    void getCountry() {
        assertEquals("Netherlands", a.getCountry());
    }

    @Test
    void getCity() {
        assertEquals("Delft", a.getCity());
    }

    @Test
    void getStreet() {
        assertEquals("Drebelweg", a.getStreet());
    }

    @Test
    void getHouseNumber() {
        assertEquals("14", a.getHouseNumber());
    }

    @Test
    void getPostalCode() {
        assertEquals("1111AA", a.getPostalCode());
    }

    @Test
    void testEquals() {
        Address b = new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA");
        assertTrue(a.equals(b));
    }
}