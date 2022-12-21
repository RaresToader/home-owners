package nl.tudelft.sem.template.authmember.domain.converters;

import nl.tudelft.sem.template.authmember.domain.Address;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.models.MembershipResponseModel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MembershipConverterTest {

    private transient Address address = new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA");

    @Test
    void convert() {
        assertEquals(new MembershipResponseModel(0L, "member1", 1L, "Netherlands", "Delft", true, null, null), MembershipConverter.convert(new Membership( "member1", 1L, address, null, null, true)));
    }

    @Test
    void convertMany() {
        List<Membership> list = new ArrayList<>();
        list.add(new Membership( "member1", 1L, address, null, null, true));
        list.add(new Membership( "member2", 1L, address, null, null, false));

        List<MembershipResponseModel> res = new ArrayList<>();
        res.add(new MembershipResponseModel(0L, "member1", 1L, "Netherlands", "Delft", true, null, null));
        res.add(new MembershipResponseModel(0L, "member2", 1L, "Netherlands", "Delft", false, null, null));
        assertEquals(res, MembershipConverter.convertMany(list));
    }

    @Test
    void constructor() {
        new MembershipConverter();
    }
}