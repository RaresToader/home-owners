package nl.tudelft.sem.template.hoa.domain.unit;

import nl.tudelft.sem.template.hoa.domain.Hoa;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



public class HoaTest {
    @Test
    void constructorTest() {
        Hoa hoa = Hoa.createHoa("germany", "hamburg", "hamburger");
        Assertions.assertNotNull(hoa);
    }

    @Test
    void createHoaTest() {
        Hoa hoa = Hoa.createHoa("germany", "hamburg", "hamburger");
        Assertions.assertEquals(hoa.getCountry(), "germany");
        Assertions.assertEquals(hoa.getCity(), "hamburg");
        Assertions.assertEquals(hoa.getName(), "hamburger");
    }

    @Test
    void equalsTest1() {
        Hoa hoa = Hoa.createHoa("germany", "hamburg", "hamburger");
        Assertions.assertEquals(hoa, hoa);
    }

    @Test
    void equalsTest2() {
        Hoa hoa = Hoa.createHoa("germany", "hamburg", "hamburger");
        Assertions.assertNotEquals(hoa, null);
    }

    @Test
    void hashCodeTest() {
        Hoa hoa = Hoa.createHoa("germany", "hamburg", "hamburger");
        Assertions.assertNotNull(hoa.hashCode());
    }
}
