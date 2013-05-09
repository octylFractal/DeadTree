package k.logix.main;

import javax.swing.JFrame;

import k.logix.util.ScreenUtil;

public class LogixMain {
	
	public static final String version = "1.0 alpha";
	public static JFrame screen = null;

	public static void main(String[] args) {
		setupScreen("logixsim v"+version);
	}

	private static void setupScreen(String title) {
		screen = new JFrame(title);
		screen.setSize(800, 600);
		ScreenUtil.center(screen);
		ScreenLoader.loadScreenByID("mainMenu");
		screen.setVisible(true);
	}

}
