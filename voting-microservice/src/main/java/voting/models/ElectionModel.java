package voting.models;

import lombok.Data;

import java.sql.Time;

@Data
public abstract class ElectionModel {
    public int hoaId;
    public String name;
    public String description;
    public Time scheduledFor;
}
