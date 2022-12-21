package nl.tudelft.sem.template.authmember.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HoaModelTest {
    
    @Test
    void testGetMemberId() {
        GetHoaModel h = new GetHoaModel();
        String id = "memberFirst";
        h.setMemberId(id);
        assertEquals(h.getMemberId(), id);
    }

    @Test
    void testGetHoaId() {
        GetHoaModel h = new GetHoaModel();
        long id = 123l;
        h.setHoaId(id);
        assertEquals(h.getHoaId(), id);
    }
}