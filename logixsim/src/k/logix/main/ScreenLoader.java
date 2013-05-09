package k.logix.main;

import javax.swing.JFrame;

public class ScreenLoader {
	private static JFrame workingFrame = null;

	public static void loadScreenByID(String id) {
		workingFrame = LogixMain.screen;
	}

}
