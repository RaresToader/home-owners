package voting.models;

import lombok.Data;

@Data
public abstract class ElectionModel {
    public int hoaId;
    public String name;
    public String description;
    public TimeModel scheduledFor;

    public boolean isValid() {
        return hoaId > 0 &&
                name != null && name.length() > 0 &&
                description != null && description.length() > 0 &&
                scheduledFor != null && scheduledFor.isValid();
    }

}
