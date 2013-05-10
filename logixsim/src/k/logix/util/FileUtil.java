package k.logix.util;

import java.io.File;

public class FileUtil {

	public static File getTop() {
		File this_dir = new File(FileUtil.class.getClassLoader()
				.getResource("FileUtil.class").getPath());
		File out = this_dir.getParentFile().getParentFile().getParentFile()
				.getParentFile();
		return out.getAbsoluteFile();
	}

}
