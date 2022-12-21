package nl.tudelft.sem.template.authmember.domain.exceptions;

import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberAlreadyExistsExceptionTest {
    @Test
    void convertToDatabaseColumn() {
        assertThrows(MemberAlreadyExistsException.class, () -> {
            throw new MemberAlreadyExistsException(new RegistrationModel());
        });
    }
}