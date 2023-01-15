package voting.models;

import java.time.LocalDateTime;

public class ElectionModel {

    public final long hoaId;
    public final String name;
    public final String description;
    /**
     * For board Election this is the scheduled time for voting
     * For proposal this is the time of the creation of the proposal
     */
    public final TimeModel scheduledFor;

    /**
     * Default constructors for [de-]serializer
     */
    public ElectionModel() {
        hoaId = 0;
        name = "";
        description = "";
        scheduledFor = null;
    }

    /**
     * Create an ElectionModel object from base fields
     *
     * @param name         Name of election
     * @param description  Description of election
     * @param hoaId        id of associated hoa
     * @param scheduledFor TimeModel to represent an election's start time
     */
    public ElectionModel(String name, String description, long hoaId, TimeModel scheduledFor) {
        this.name = name;
        this.description = description;
        this.hoaId = hoaId;
        this.scheduledFor = scheduledFor;
    }

    /**
     * Create an ElectionModel object from base fields
     *
     * @param name        Name of election
     * @param description Description of election
     * @param hoaId       id of associated hoa
     * @param sf          LocalDateTime to represent an election's start time
     */
    public ElectionModel(String name, String description, long hoaId, LocalDateTime sf) {
        this(name, description, hoaId, new TimeModel(sf.getSecond(), sf.getMinute(),
                sf.getHour(), sf.getDayOfMonth(), sf.getMonthValue(), sf.getYear()));
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

    @Override
    public boolean equals(Object o) {
        return this.getClass() == o.getClass() && this == o;
    }
}
