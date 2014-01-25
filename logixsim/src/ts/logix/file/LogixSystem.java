package ts.logix.file;

import java.io.File;
import java.io.FileOutputStream;

public class LogixSystem {
    public static final String fileext = "lsys";
    private String id = "";

    public LogixSystem(String name) {
        id = name;
    }

    /**
     * Saves this system to the given directory.
     * 
     * @param dir
     *            - the directory to save to
     */
    public void save(File dir) {
        try {
            File save = new File(dir, id + fileext);
            FileOutputStream fos = new FileOutputStream(save);
            fos.write(toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return id;
    }

    public static LogixSystem load(File save) {
        LogixSystem ls = new LogixSystem(save.getName().replace(fileext, ""));
        return ls;
    }

}
