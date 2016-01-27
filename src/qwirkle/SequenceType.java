package qwirkle;

/**
 * Provides values for valid types of a sequence
 * on the Qwirkle board.
 * @author Gebruiker
 *
 */
public enum SequenceType {
	/**
	 * Indicates the type is unknown (i.e. the sequence has just one stone).
	 */
	UNKNOWN,
	
	/**
	 * Indicates the sequence consists of stones with the same shape.
	 */
	SHAPE,
	
	/**
	 * Indicates the sequence consists of stones with the same color.
	 */
	COLOR
}
