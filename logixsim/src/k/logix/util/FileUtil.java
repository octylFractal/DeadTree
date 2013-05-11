package k.logix.util;

import java.io.File;
import java.net.URISyntaxException;

public class FileUtil {

	public static File getTop() {
		File this_dir = null;
		try {
			this_dir = new File(FileUtil.class.getClassLoader()
					.getResource("k/logix/util/FileUtil.class").toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			System.exit(1);
		}
		File out = this_dir.getParentFile().getParentFile().getParentFile()
				.getParentFile();
		return out.getAbsoluteFile();
	}

}
