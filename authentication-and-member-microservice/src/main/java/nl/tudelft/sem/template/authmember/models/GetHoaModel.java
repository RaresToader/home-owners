package nl.tudelft.sem.template.authmember.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Model representing a HOA get/leave request.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class GetHoaModel extends HoaModel {
    private String memberId;
    private long hoaId;
}