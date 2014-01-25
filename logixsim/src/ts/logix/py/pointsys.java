package ts.logix.py;

import java.util.List;

import k.core.util.jythonintegration.JythonClass;
import k.core.util.jythonintegration.JythonFile;
import k.core.util.jythonintegration.JythonIntergration;

import org.python.core.PyBoolean;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;

public class pointsys {
    public static final JythonFile file = JythonIntergration
            .getFile("./python/pointsys.py");

    public static class PyPoint {
        public static final JythonClass jc = file.getJClass("Point");
        private static final PyString state = new PyString("state");
        public PyObject obj = null;

        public PyPoint() {
            obj = jc.newInstance();
        }

        public void markConnection(PyPoint p2, boolean connected) {
            jc.invokeMethod("markConnection", obj, p2.obj, new PyBoolean(
                    connected));
        }

        public boolean isConnected(PyPoint p2) {
            return Boolean.parseBoolean(jc.invokeMethod("isConnected", obj,
                    p2.obj).toString());
        }

        public void pushState(boolean state) {
            jc.invokeMethod("pushState", obj, new PyBoolean(state));
        }

        public boolean state() {
            return Boolean.parseBoolean(obj.__getattr__(state).toString());
        }

        @Override
        public String toString() {
            return obj.__repr__().toString();
        }
    }

    public static void writeConnection(PyPoint p1, List<PyPoint> connList) {
        PyList list = new PyList(connList);
        file.invokeMethod("writeConnection", p1.obj, list);
    }

    public static void addConnection(PyPoint p1, PyPoint p2) {
        file.invokeMethod("addConnection", p1.obj, p2.obj);
    }

    public static void remConnection(PyPoint p1, PyPoint p2) {
        file.invokeMethod("remConnection", p1.obj, p2.obj);
    }
}
