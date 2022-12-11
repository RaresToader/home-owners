package nl.tudelft.sem.template.hoa.domain.unit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.sem.template.hoa.exception.ActivityDoesntExistException;
import org.junit.jupiter.api.Test;

public class ActivityDoesntExistExceptionTest {
    @Test
    void constructorTest() {
        ActivityDoesntExistException exception = new ActivityDoesntExistException("error");
        assertNotNull(exception);
    }
}
