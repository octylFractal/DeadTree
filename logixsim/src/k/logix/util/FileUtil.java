package k.logix.util;

import java.io.File;

public class FileUtil {

	public static File getTop() {
		return new File("").getAbsoluteFile();
	}

}
