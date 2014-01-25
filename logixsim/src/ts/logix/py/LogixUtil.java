package ts.logix.py;

import java.util.List;

import ts.logix.py.GateSys.Gate;
import ts.logix.py.PointSys.PyPoint;

public class LogixUtil {
    private LogixUtil() {
    }

    public static void connect(PyPoint p1, PyPoint p2) {
        p1.markConnection(p2, State.CONNECTED);
    }
    
    public static void disconnect(PyPoint p1, PyPoint p2) {
        p1.markConnection(p2, State.DISCONNECTED);
    }

    public static void wireInputsToGate(List<PyPoint> inputs, Gate g) {
        for (int i = 0; i < inputs.size(); i++) {
            connect(inputs.get(i), g.inputs.get(i));
        }
    }

    public static void wireGateToOutputs(Gate g, List<PyPoint> outputs) {
        for (int i = 0; i < outputs.size(); i++) {
            connect(g.outputs.get(i), outputs.get(i));
        }
    }
}
