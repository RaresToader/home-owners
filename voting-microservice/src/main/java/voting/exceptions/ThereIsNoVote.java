package voting.exceptions;

public class ThereIsNoVote extends Exception{

	static final long serialVersionUID = -3521454513612556248L;

	/**
	 * Constructor for the exception.
	 *
	 * @param message the error message
	 */
	public ThereIsNoVote(String message) {
		super(message);
	}
}
