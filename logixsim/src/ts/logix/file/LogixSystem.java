package ts.logix.file;

import java.io.File;

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
        File save = new File(dir, id + fileext);
    }
    
    @Override
    public String toString() {
        return id;
    }

}
