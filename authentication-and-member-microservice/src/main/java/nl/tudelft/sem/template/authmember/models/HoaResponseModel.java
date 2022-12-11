package nl.tudelft.sem.template.authmember.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing a Hoa Response.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoaResponseModel {
    private String country;
    private String city;
    private String name;
}
