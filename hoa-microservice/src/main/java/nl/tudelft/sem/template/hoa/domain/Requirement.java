package nl.tudelft.sem.template.hoa.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.hoa.annotations.Generated;


/**
 * Class that holds a requirement.
 */
@Generated
@Entity
@Table(name = "Requirement")
@NoArgsConstructor
public class Requirement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id", nullable = false)
    private long id;
    @Column(name = "prompt", nullable = false)
    private String prompt;

    @Column(name = "HoaId", nullable = false)
    private long hoaId;

    /**
     * Public constructor for the Requirement class.
     *
     * @param prompt the description for the requirement.
     * @param hoaId       the HOA that has this requirement.
     */
    public Requirement(String prompt, long hoaId) {
        this.prompt = prompt;
        this.hoaId = hoaId;
    }

    /**
     * Getter for the id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for the prompt.
     *
     * @return the prompt.
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Getter for the Hoa id.
     *
     * @return the hoaId
     */
    public long getHoaId() {
        return hoaId;
    }

    /**
     * Equals method for the Requirement class.
     *
     * @param o an object
     * @return true if this is equal to o, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Requirement that = (Requirement) o;
        return hoaId == that.hoaId && Objects.equals(prompt, that.prompt);
    }

    /**
     * Hashcode implementation for requirement class.
     *
     * @return an int, representing the hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, prompt, hoaId);
    }
}
