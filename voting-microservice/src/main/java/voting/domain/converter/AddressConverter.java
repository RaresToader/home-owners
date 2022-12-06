package voting.domain.converter;

import voting.domain.Address;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the Address object.
 */
@Converter
public class AddressConverter implements AttributeConverter<Address, String> {

    @Override
    public String convertToDatabaseColumn(Address address) {
        return address.toDBString();
    }

    @Override
    public Address convertToEntityAttribute(String dbData) {
        return new Address(dbData);
    }

}