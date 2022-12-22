package nl.tudelft.sem.template.authmember.domain.converters;

import nl.tudelft.sem.template.authmember.domain.Address;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.models.MembershipResponseModel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MembershipConverterTest {

    private transient String country = "Netherlands";
    private transient String city = "Delft";
    private transient String m1 = "member1";
    private transient String m2 = "member2";
    private transient Address address = new Address(country, city, "Drebelweg", "14", "1111AA");

    @Test
    void convert() {
        assertEquals(new MembershipResponseModel(0L, m1,
                1L, country, city, true, null, null),
                MembershipConverter.convert(new Membership(m1, 1L, address,
                        null, null, true)));
    }

    @Test
    void convertMany() {
        List<Membership> list = new ArrayList<>();
        list.add(new Membership(m1, 1L, address, null, null, true));
        list.add(new Membership(m2, 1L, address, null, null, false));

        List<MembershipResponseModel> res = new ArrayList<>();
        res.add(new MembershipResponseModel(0L, m1, 1L, country, city, true, null, null));
        res.add(new MembershipResponseModel(0L, m2, 1L, country, city, false, null, null));
        assertEquals(res, MembershipConverter.convertMany(list));
    }

    @Test
    void constructor() {
        new MembershipConverter();
    }
}