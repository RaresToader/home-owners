package nl.tudelft.sem.template.hoa.domain.unit;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.UNIT;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import nl.tudelft.sem.template.hoa.exception.ActivityDoesntExistException;
import org.junit.jupiter.api.Test;

@TestSuite(testType = UNIT)
public class ActivityDoesntExistExceptionTest {
    @Test
    void constructorTest() {
        ActivityDoesntExistException exception = new ActivityDoesntExistException("error");
        assertNotNull(exception);
    }
}
