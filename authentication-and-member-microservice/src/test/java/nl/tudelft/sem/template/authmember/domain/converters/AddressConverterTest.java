package nl.tudelft.sem.template.authmember.domain.converters;

import nl.tudelft.sem.template.authmember.domain.Address;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressConverterTest {

    @Test
    void convertToDatabaseColumn() {
        AddressConverter a = new AddressConverter();
        assertEquals("Netherlands,Delft,Drebelweg,14,1111AA",
                a.convertToDatabaseColumn(
                        new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA")));
    }

    @Test
    void convertToEntityAttribute() {
        AddressConverter a = new AddressConverter();
        assertEquals(new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA"),
                a.convertToEntityAttribute("Netherlands,Delft,Drebelweg,14,1111AA"));
    }
}