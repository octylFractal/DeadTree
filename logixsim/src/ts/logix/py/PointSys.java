package ts.logix.py;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The matching Python file is named pointsys.py.
 * 
 * @author Kenzie Togami
 */
public class PointSys {
    /**
     * The map of a {@link PyPoint} to a list of connections.
     */
    protected static final HashMap<PyPoint, List<PyPoint>> connections = new HashMap<PyPoint, List<PyPoint>>();
    /**
     * The next point id.
     */
    private static int nextId = 0;

    /**
     * Puts the connection list in the map of connections under the given point.
     * 
     * @param point
     *            - the 'key'
     * @param connList
     *            - the 'value'
     */
    public static void writeConnection(PyPoint point, List<PyPoint> connList) {
        connections.put(point, connList);
    }

    /**
     * Creates a new connection <tt>from -> to</tt>
     * 
     * @param from
     *            - the point to connect from
     * @param to
     *            - the point to connect to
     */
    public static void addConnection(PyPoint from, PyPoint to) {
        List<PyPoint> cl = connections.get(from);
        if (cl.contains(to)) {
            System.err.println("Skipping already connected point " + to + ".");
            return;
        }
        cl.add(to);
        to.pushState(from.state);
    }

    /**
     * Removes the connection <tt>from -> to</tt>
     * 
     * @param from
     *            - the point to disconnect from <tt>to</tt>
     * @param to
     *            - the point to disconnect from
     */
    public static void remConnection(PyPoint from, PyPoint to) {
        connections.get(from).remove(to);
    }

    /**
     * A class that provides different connectable points that can be used to
     * perform logic.
     * 
     * @author Kenzie Togami
     */
    public static class PyPoint {
        protected boolean state = false;
        protected int id = 0;

        public PyPoint() {
            id = nextId++;
            state = State.OFF;
            writeConnection(this, new ArrayList<PyPoint>());
        }

        public void markConnection(PyPoint p2, boolean connected) {
            if (connected) {
                addConnection(this, p2);
            } else {
                remConnection(this, p2);
            }
        }

        public boolean isConnected(PyPoint p2) {
            return connections.get(this).contains(p2);
        }

        /**
         * WARNING: If connections are two-way, this goes recursive and causes a
         * {@link StackOverflowError}.
         */
        public void pushState(boolean state) {
            this.state = state;
            List<PyPoint> dup = new ArrayList<PointSys.PyPoint>(
                    connections.get(this));
            for (PyPoint connection : dup) {
                connection.pushState(state);
            }
        }

        public boolean state() {
            return state;
        }

        public int id() {
            return id;
        }

        @Override
        public String toString() {
            return "(point" + id + ": state:" + Conversion.getStateAsStr(state)
                    + "; conns:" + connections.get(this) + ")";
        }
    }
}
