package nl.tudelft.sem.template.authmember.models;

import lombok.Data;

/**
 * Model representing a HOA get/leave request.
 */
@Data
public class GetHOAModel extends HOAModel{
    private String memberID;
    private int hoaID;
}