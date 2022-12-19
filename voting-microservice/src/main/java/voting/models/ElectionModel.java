package voting.models;

import lombok.Data;

@Data
public class ElectionModel {
    public int hoaId;
    public String name;
    public String description;
    public TimeModel scheduledFor;

    /**
     * Overwrite base DTO constructor to change visibility to protected (cannot be instantiated)
     */
    protected ElectionModel() {
        // This constructor cannot be called, hence it is left empty.
    }

    /**
     * Checks whether this model is a valid one for creating an election
     *
     * @return Boolean to represent the model's validity
     */
    public boolean isValid() {
        return hoaId > 0
                && name != null
                && name.length() > 0
                && name.length() < 90
                && description != null
                && description.length() > 0
                && description.length() < 240
                && scheduledFor != null
                && scheduledFor.isValid();
    }

}
