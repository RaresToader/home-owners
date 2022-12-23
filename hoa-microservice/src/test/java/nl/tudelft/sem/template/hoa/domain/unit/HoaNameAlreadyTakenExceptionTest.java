package nl.tudelft.sem.template.hoa.domain.unit;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.UNIT;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import nl.tudelft.sem.template.hoa.exception.HoaNameAlreadyTakenException;
import org.junit.jupiter.api.Test;

@TestSuite(testType = UNIT)
public class HoaNameAlreadyTakenExceptionTest {
    @Test
    void constructorTest() {
        HoaNameAlreadyTakenException exception = new HoaNameAlreadyTakenException("error");
        assertNotNull(exception);
    }
}
