package nl.tudelft.sem.template.hoa.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProposalRequestModel {
    public long hoaId;
    public String name;
    public String description;
    public TimeModel scheduledFor;
}
