package voting.db.converter;

import org.junit.jupiter.api.Test;
import voting.annotations.TestSuite;
import voting.db.converters.AddressConverter;
import voting.domain.Address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static voting.annotations.TestSuite.TestType.UNIT;

@TestSuite(testType = UNIT)
class AddressConverterTest {
    AddressConverter sut = new AddressConverter();

    @Test
    void convertToDatabaseColumnTest() {
        Address addr = new Address("", "", "", "", "");
        String result = sut.convertToDatabaseColumn(addr);
        assertEquals(",,,,", result);

        addr = new Address("country", "city", "street", "122a",
                "9999AB");
        result = sut.convertToDatabaseColumn(addr);
        assertEquals("country,city,street,122a,9999AB", result);
    }

    @Test
    void convertToEntityAttributeTest() {
        String testStr = "";
        Address addr = sut.convertToEntityAttribute(testStr);
        assertEquals("", addr.getCountry());
        assertEquals("", addr.getCity());
        assertEquals("", addr.getStreet());
        assertEquals("", addr.getHouseNumber());
        assertEquals("", addr.getPostalCode());

        testStr = "countryX,cityY,streetZ,20xyz,6969JK";
        addr = sut.convertToEntityAttribute(testStr);
        assertEquals("countryX", addr.getCountry());
        assertEquals("cityY", addr.getCity());
        assertEquals("streetZ", addr.getStreet());
        assertEquals("20xyz", addr.getHouseNumber());
        assertEquals("6969JK", addr.getPostalCode());
    }
}
