package nl.tudelft.sem.template.authmember.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sem.template.authmember.domain.Address;

/**
 * Model representing a registration request.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinHoaModel  extends HoaModel {
    private String memberId;
    private long hoaId;
    private Address address;

    @Override
    public boolean equals(Object o) {
        if (o instanceof JoinHoaModel) {
            JoinHoaModel model = (JoinHoaModel) o;
            return model.getMemberId().equals(this.memberId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return memberId.hashCode();
    }
}