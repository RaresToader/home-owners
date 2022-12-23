package nl.tudelft.sem.template.authmember.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Model representing a Hoa Response.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoaResponseModel {
    private long id;
    private String country;
    private String city;
    private String name;
    private Map<String, List<Long>> reports;
    private Map<String, List<String>> notifications;
}
