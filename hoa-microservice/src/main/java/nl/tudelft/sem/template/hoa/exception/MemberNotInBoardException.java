package nl.tudelft.sem.template.hoa.exception;

import nl.tudelft.sem.template.hoa.annotations.Generated;

@Generated
public class MemberNotInBoardException extends Exception {

	static final long serialVersionUID = -3387576157424229948L;

	/**
	 * Constructor for the member not in a board exception.
	 *
	 */
	public MemberNotInBoardException(String message) {
		super(message);
	}
}
