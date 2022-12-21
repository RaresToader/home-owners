package nl.tudelft.sem.template.authmember.domain.exceptions;

import nl.tudelft.sem.template.authmember.domain.Address;
import nl.tudelft.sem.template.authmember.domain.converters.AddressConverter;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberDifferentAddressExceptionTest {
    @Test
    void convertToDatabaseColumn() {
        assertThrows(MemberDifferentAddressException.class, () -> {
            throw new MemberDifferentAddressException(new JoinHoaModel());
        });
    }
}