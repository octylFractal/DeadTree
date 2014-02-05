package ts.deadtree.py;

import java.util.List;

import ts.deadtree.py.GateSys.Gate;
import ts.deadtree.py.PointSys.PyPoint;

/**
 * The matching Python file is named logixmain__.py.
 * 
 * @author Kenzie Togami
 */
public class LogixUtil {
    private LogixUtil() {
    }

    /**
     * Connects p1 ---> p2
     * 
     * @param p1
     *            - point one
     * @param p2
     *            - point two
     * @see PyPoint#markConnection(PyPoint, boolean)
     */
    public static void connect(PyPoint p1, PyPoint p2) {
        p1.markConnection(p2, State.CONNECTED);
    }

    /**
     * Disconnects p1 -/-> p2
     * 
     * @param p1
     *            - point one
     * @param p2
     *            - point two
     * @see PyPoint#markConnection(PyPoint, boolean)
     */
    public static void disconnect(PyPoint p1, PyPoint p2) {
        p1.markConnection(p2, State.DISCONNECTED);
    }

    /**
     * Wires the input list to the gates input list.
     * 
     * @param inputs
     *            - the inputs to attach
     * @param g
     *            - the gate to attach to
     * @see LogixUtil#connect(PyPoint, PyPoint)
     */
    public static void wireInputsToGate(List<PyPoint> inputs, Gate g) {
        for (int i = 0; i < inputs.size(); i++) {
            connect(inputs.get(i), g.inputs.get(i));
        }
    }

    /**
     * Wires the gates output list to the output list.
     * 
     * @param g
     *            - the gate to attach from
     * @param outputs
     *            - the outputs to attach to
     * @see LogixUtil#connect(PyPoint, PyPoint)
     */
    public static void wireGateToOutputs(Gate g, List<PyPoint> outputs) {
        for (int i = 0; i < outputs.size(); i++) {
            connect(g.outputs.get(i), outputs.get(i));
        }
    }
}
