package nl.tudelft.sem.template.authmember.domain.exceptions;

import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadRegistrationModelExceptionTest {
    @Test
    void convertToDatabaseColumn() {
        assertThrows(BadRegistrationModelException.class, () -> {
            throw new BadRegistrationModelException("bad reg");
        });
    }
}