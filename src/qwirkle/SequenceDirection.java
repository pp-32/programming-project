package qwirkle;

/**
 * Provides values for valid directions of a sequence 
 * on the Qwirkle board. 
 * @author Jerre
 *
 */
public enum SequenceDirection {
	/**
	 * Indicates the direction is unknown (i.e. the sequence has just one element.)
	 */
	UNKNOWN,
	
	/**
	 * Indicates the sequence is a horizontal sequence.
	 */
	HORIZONTAL,
	
	/**
	 * Indicates the sequence is a vertical sequence.
	 */
	VERTICAL
}
