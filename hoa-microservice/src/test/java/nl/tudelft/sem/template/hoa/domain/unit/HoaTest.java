package nl.tudelft.sem.template.hoa.domain.unit;

import nl.tudelft.sem.template.hoa.domain.Hoa;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



public class HoaTest {

    private static final  String CTR = "Germany";

    private static final String CITY = "Hamburg";

    private static final String STR = "HamburgerLmao";

    @Test
    void constructorTest() {
        Hoa hoa = Hoa.createHoa(CTR, CITY, STR);
        Assertions.assertNotNull(hoa);
    }

    @Test
    void createHoaTest() {
        Hoa hoa = Hoa.createHoa(CTR, CITY, STR);
        Assertions.assertEquals(hoa.getCountry(), CTR);
        Assertions.assertEquals(hoa.getCity(), CITY);
        Assertions.assertEquals(hoa.getName(), STR);
    }

    @Test
    void equalsTest1() {
        Hoa hoa = Hoa.createHoa(CTR, CITY, STR);
        Assertions.assertEquals(hoa, hoa);
    }

    @Test
    void equalsTest2() {
        Hoa hoa = Hoa.createHoa(CTR, CITY, STR);
        Assertions.assertNotEquals(hoa, null);
    }

    @Test
    void hashCodeTest() {
        Hoa hoa = Hoa.createHoa(CTR, CITY, STR);
        Assertions.assertNotNull(hoa.hashCode());
    }
}
