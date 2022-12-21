package nl.tudelft.sem.template.authmember.domain.exceptions;

import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;


class MemberAlreadyInHoaExceptionTest {
    @Test
    void convertToDatabaseColumn() {
        assertThrows(MemberAlreadyInHoaException.class, () -> {
            throw new MemberAlreadyInHoaException(new JoinHoaModel());
        });
    }
}