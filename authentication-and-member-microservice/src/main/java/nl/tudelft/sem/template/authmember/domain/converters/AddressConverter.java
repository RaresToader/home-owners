package nl.tudelft.sem.template.authmember.domain.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import nl.tudelft.sem.template.authmember.domain.Address;


/**
 * JPA Converter for the Address object.
 */
@Converter
public class AddressConverter implements AttributeConverter<Address, String> {

    @Override
    public String convertToDatabaseColumn(Address address) {
        return address.toDbString();
    }

    @Override
    public Address convertToEntityAttribute(String dbData) {
        return new Address(dbData);
    }

}
