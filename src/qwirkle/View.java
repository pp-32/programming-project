package qwirkle;

public interface View extends Runnable {
		
	/**
	 * Updates the TUI.	
	 */
	public void updateView();
	
	/**
	 * Builds the TUI.
	 */
	public void buildView();
}
