package qwirkle;

import java.util.Observer;

public interface View extends Runnable, Observer {
		
	/**
	 * Updates the TUI.	
	 */
	public void updateView();
	
	/**
	 * Builds the TUI.
	 */
	public void buildView();
	
	public void showError(String reason);
}
