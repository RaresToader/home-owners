package nl.tudelft.sem.template.authmember.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BadRegistrationModelExceptionTest {
    @Test
    void convertToDatabaseColumn() {
        assertThrows(BadRegistrationModelException.class, () -> {
            throw new BadRegistrationModelException("bad reg");
        });
    }
}