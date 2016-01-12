package qwirkle;

/**
 * Represents a board in the Qwirkle game.
 * @author Jerre
 *
 */
public interface Board {
	
	/**
	 * Creates a deep copy of the board.
	 * @return the copy.
	 */
	public Board deepCopy();
		
	/**
	 * Resets the board, clearing all stones from the field.
	 */
	public void reset();
	
	/**
	 * Gets the stone that is located at the given position.
	 * @param x The x coordinate of the position.
	 * @param y The y coordinate of the position.
	 * @return The stone, or null if the field is empty.
	 */
	public Stone getField(int x, int y);
	
	/**
	 * Determines whether a field at the given coordinates is empty or not.
	 * @param x The x coordinate of the position.
	 * @param y The y coordinate of the position.
	 * @return True if the field is empty, false otherwise.
	 */
	public boolean isEmptyField(int x, int y);
	
	/**
	 * Determines whether the board is empty or not.
	 * @return True if the board is empty, false otherwise.
	 */
	public boolean isEmpty();
	
	/**
	 * Determines whether a stone can be picked up from the pile.
	 * @return True if the pile is not empty, false otherwise.
	 */
	public boolean canPickStone();
	
	/**
	 * Grabs a stone from the pile.
	 * @return The Stone that was picked up.
	 */
	public Stone pickStone();
	
	/**
	 * Determines whether a placement of a specific stone at the given location
	 * is a legal move or not.
	 * @param stone The stone to place.
	 * @param x The x-coordinate of the field to place the stone on.
	 * @param y The y-coordinate of the field to place the stone on.
	 * @return True if the move is legal, false otherwise.
	 */
	public boolean checkMove(Stone stone, int x, int y);
	
	/**
	 * Places a stone at the given location.
	 * @param stone The stone to place.
	 * @param x The x-coordinate of the field to place the stone on.
	 * @param y The y-coordinate of the field to place the stone on.
	 */
	public void placeStone(Stone stone, int x, int y);
}
