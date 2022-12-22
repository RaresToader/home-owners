package nl.tudelft.sem.template.authmember.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HoaModelTest {

    @Test
    void testGetMemberId() {
        GetHoaModel h = new GetHoaModel();
        String id = "memberFirst";
        h.setMemberId(id);
        assertEquals(id, h.getMemberId());
    }

    @Test
    void testGetMemberIdEmpty() {
        HoaModel h = new GetHoaModel();
        assertNull(h.getMemberId());
    }

    @Test
    void testGetHoaId() {
        GetHoaModel h = new GetHoaModel();
        long id = 123L;
        h.setHoaId(id);
        assertEquals(id, h.getHoaId());
    }

    @Test
    void testGetHoaIdEmpty() {
        HoaModel h = new GetHoaModel();
        assertEquals(0, h.getHoaId());
    }
}