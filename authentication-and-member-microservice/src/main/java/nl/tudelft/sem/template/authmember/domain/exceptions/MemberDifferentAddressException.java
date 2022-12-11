package nl.tudelft.sem.template.authmember.domain.exceptions;

import nl.tudelft.sem.template.authmember.models.JoinHoaModel;

/**
 * This is an exception thrown if the user wanting to join an HOA has a different country or city
 * compared to the hoa to be joined.
 */
public class MemberDifferentAddressException extends Exception {
    static final long serialVersionUID = -3371223133154229948L;

    /**
     * Constructor for the member different address exception.
     *
     * @param model the join hoa model.
     */
    public MemberDifferentAddressException(JoinHoaModel model) {
        super(model.getMemberId() + " " + model.getHoaId());
    }
}
