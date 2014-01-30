package ts.logix.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileNameExtensionFilter;

import k.core.util.Helper.BetterArrays;
import k.core.util.netty.DataStruct;
import ts.logix.positionables.Positionable;

public class LogixSystem {
    public static final String fileext = "lsys";
    public static final FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "logixsim Files", fileext);
    private String id = "";
    public List<Positionable> objs = new ArrayList<Positionable>();

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
        return asDataStruct().toString();
    }

    private DataStruct asDataStruct() {
        DataStruct out = new DataStruct(new Object[] { id });
        for (Object o : objs) {
            out.add(o);
        }
        return out;
    }

    public static LogixSystem load(File save) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(save)));
            String input = "";
            for (String l = br.readLine(); l != null; l = br.readLine()) {
                input += l;
            }
            br.close();
            DataStruct in = new DataStruct(input);
            Object[] data = in.getAll();
            String id = (String) data[0];
            LogixSystem ls = new LogixSystem(id);
            Object[] spliced = BetterArrays.splice(data, 1, data.length, 1);
            for (Object o : spliced) {
                ls.objs.add((Positionable) o);
            }
            return ls;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
