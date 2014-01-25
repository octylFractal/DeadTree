package ts.logix.py;

import k.core.util.jythonintegration.JythonFile;
import k.core.util.jythonintegration.JythonIntergration;
import ts.logix.py.pointsys.PyPoint;

public class logixmain__ {
    private static JythonFile file = JythonIntergration
            .getFile("./python/logixmain__.py");

    private logixmain__() {
    }

    public static void connect(PyPoint p1, PyPoint p2) {
        file.invokeMethod("connect", p1.obj, p2.obj);
    }
}
