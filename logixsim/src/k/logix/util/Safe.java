package k.logix.util;

public class Safe {

	public static int intCast(String string) {
		try {
			return Integer.parseInt(string);
		} catch (Exception e) {
			if(string != null) {
				if(string.equals("")) {
					return 0;
				}
			}
		}
		throw new IllegalArgumentException("'" + string
				+ "' is not a valid integer!");
	}

}
