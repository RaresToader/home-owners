package nl.tudelft.sem.template.authmember.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


class BadJoinHoaModelExceptionTest {
    @Test
    void convertToDatabaseColumn() {
        assertThrows(BadJoinHoaModelException.class, () -> {
            throw new BadJoinHoaModelException("bad hoa");
        });
    }
}