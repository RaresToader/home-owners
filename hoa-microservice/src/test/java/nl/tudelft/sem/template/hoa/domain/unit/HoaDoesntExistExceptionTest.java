package nl.tudelft.sem.template.hoa.domain.unit;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.UNIT;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import org.junit.jupiter.api.Test;

@TestSuite(testType = UNIT)
public class HoaDoesntExistExceptionTest {

    @Test
    void constructorTest() {
        HoaDoesntExistException exception = new HoaDoesntExistException("error");
        assertNotNull(exception);
    }
}
