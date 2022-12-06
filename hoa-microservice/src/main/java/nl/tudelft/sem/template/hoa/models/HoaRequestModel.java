package nl.tudelft.sem.template.hoa.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing an HoaModel request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoaRequestModel {
    private String country;
    private String city;
    private String name;

}
