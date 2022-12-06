package nl.tudelft.sem.template.authmember.models;

import lombok.Data;
import nl.tudelft.sem.template.authmember.domain.Address;

/**
 * Model representing a registration request.
 */
@Data
public class JoinHOAModel  extends HOAModel {
    private String memberID;
    private int hoaID;
    private Address address;
}