package nl.tudelft.sem.template.authmember.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Model representing a HOA get/leave request.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GetHoaModel extends HoaModel {
    private String memberId;
    private long hoaId;
}