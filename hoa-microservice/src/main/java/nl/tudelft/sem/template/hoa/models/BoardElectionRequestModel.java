package nl.tudelft.sem.template.hoa.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardElectionRequestModel {
    public int hoaId;
    public int amountOfWinners;
    public List<Integer> candidates;
    public String name;
    public String description;
    public TimeModel scheduledFor;
}
