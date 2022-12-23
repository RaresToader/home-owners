package boundary;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.BOUNDARY;
import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.UNIT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.db.HoaService;
import nl.tudelft.sem.template.hoa.db.RequirementRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@TestSuite(testType = {BOUNDARY, UNIT})
public class HoaServiceBoundaryTest {
    @Mock
    private transient HoaRepo hoaRepo;

    @Mock
    private transient RequirementRepo reqRepo;

    private transient HoaService hoaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        hoaService = new HoaService(hoaRepo, reqRepo);
    }

    @Test
    void countryCheckOffPoint() {
        String sb = "A" + "a".repeat(50);
        assertTrue(hoaService.countryCheck(sb));
    }

    @Test
    void countryCheckOnPoint() {
        String sb = "A" + "a".repeat(49);
        assertFalse(hoaService.countryCheck(sb));
    }

    @Test
    void nameCheckOffPoint() {
        String sb = "A" + "a".repeat(50);
        assertFalse(hoaService.nameCheck(sb));
    }

    @Test
    void nameCheckOnPoint() {
        String sb = "A" + "a".repeat(49);
        assertTrue(hoaService.nameCheck(sb));
    }

    @Test
    void enoughCharsOffPoint() {
        String string = "A a a ";
        assertFalse(hoaService.enoughCharsAndWhitespace(string));

    }

    @Test
    void enoughCharsOnPoint() {
        String string = "A a a a";
        assertTrue(hoaService.enoughCharsAndWhitespace(string));
    }
}
