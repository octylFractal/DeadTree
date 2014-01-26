package ts.logix.file;

import java.io.File;
import java.io.FileOutputStream;

import ts.logix.interfaces.GridObject;
import ts.logix.py.GateSys;
import ts.logix.py.GateSys.Gate;
import ts.logix.py.LogixUtil;
import ts.logix.py.PointSys.PyPoint;

public class LogixSystem {
    public static final String fileext = "lsys";
    private String id = "";
    private GridObject[][] grid = new GridObject[0][0];

    public LogixSystem(String name, int width, int height) {
        id = name;
        grid = new GridObject[width][height];
    }

    public PyPoint createPoint(int x, int y) {
        PyPoint p = (PyPoint) grid[x][y];
        if (p == null) {
            p = new PyPoint();
        }
        grid[x][y] = p;
        return p;
    }

    public PyPoint removePoint(int x, int y) {
        PyPoint p = (PyPoint) grid[x][y];
        grid[x][y] = null;
        return p;
    }

    public PyPoint pointAt(int x, int y) {
        return (PyPoint) grid[x][y];
    }

    public void connect(int x1, int y1, int x2, int y2) {
        LogixUtil.connect(pointAt(x1, y1), pointAt(x2, y2));
    }

    public void disconnect(int x1, int y1, int x2, int y2) {
        LogixUtil.disconnect(pointAt(x1, y1), pointAt(x2, y2));
    }

    public Gate createGate(int x, int y, String id, int inputs, int outputs) {
        Gate g = (Gate) grid[x][y];
        if (g == null) {
            g = GateSys.newGate(id).setInputs(inputs).setOutputs(outputs)
                    .build();
        }
        grid[x][y] = g;
        return g;
    }

    public Gate putGate(int x, int y, Gate g) {
        grid[x][y] = g;
        return g;
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
        throw new UnsupportedOperationException();
    }

}
