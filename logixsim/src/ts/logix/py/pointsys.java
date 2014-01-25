package ts.logix.py;

import k.core.util.jythonintegration.JythonClass;
import k.core.util.jythonintegration.JythonFile;
import k.core.util.jythonintegration.JythonIntergration;

import org.python.core.PyBoolean;
import org.python.core.PyObject;

public class pointsys {
    public static final JythonFile file = JythonIntergration
            .getFile("./python/pointsys.py");

    public static class PyPoint {
        public static final JythonClass jc = file.getJClass("Point");
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

        @Override
        public String toString() {
            return obj.__repr__().toString();
        }
    }
}
