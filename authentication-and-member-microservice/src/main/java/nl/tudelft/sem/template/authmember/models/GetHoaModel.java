package nl.tudelft.sem.template.authmember.models;

import lombok.Data;

/**
 * Model representing a HOA get/leave request.
 */
@Data
public class GetHoaModel extends HoaModel {
    private String memberId;
    private int hoaId;
}