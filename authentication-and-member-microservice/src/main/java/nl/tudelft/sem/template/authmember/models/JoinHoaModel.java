package nl.tudelft.sem.template.authmember.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.sem.template.authmember.domain.Address;

/**
 * Model representing a registration request.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JoinHoaModel  extends HoaModel {
    private String memberId;
    private long hoaId;
    private Address address;
}