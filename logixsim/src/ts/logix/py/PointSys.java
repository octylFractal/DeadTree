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
        /**
         * The state of this point, either {@link State#ON} or {@link State#OFF}
         * .
         */
        protected boolean state = false;
        /**
         * The id of this point.
         */
        protected int id = 0;

        /**
         * Creates a new point with the next available id and state set to
         * {@link State#OFF}.
         */
        public PyPoint() {
            id = nextId++;
            state = State.OFF;
            writeConnection(this, new ArrayList<PyPoint>());
        }

        /**
         * Creates or deletes a connection from <tt>this</tt> to <tt>p2</tt>.
         * 
         * @param p2
         *            - the point to (dis)connect to.
         * @param connected
         *            - if this is a connection or disconnection
         * @see State#CONNECTED
         * @see State#DISCONNECTED
         */
        public void markConnection(PyPoint p2, boolean connected) {
            if (connected) {
                addConnection(this, p2);
            } else {
                remConnection(this, p2);
            }
        }

        /**
         * Returns {@link State#CONNECTED} if this is connected, or
         * {@link State#DISCONNECTED} if it is not.
         * 
         * @param p2
         *            - the point to check against
         * @return {@link State#CONNECTED} or {@link State#DISCONNECTED}
         */
        public boolean isConnected(PyPoint p2) {
            return connections.get(this).contains(p2);
        }

        /**
         * Pushes <tt>state</tt> to this point and all it's connections.
         * Recursive.<br>
         * <br>
         * WARNING: If connections are two-way, this goes over the same point
         * repeatedly and causes a {@link StackOverflowError}.
         * 
         * @param state
         *            - {@link State#ON} or {@link State#OFF}.
         */
        public void pushState(boolean state) {
            this.state = state;
            List<PyPoint> dup = new ArrayList<PointSys.PyPoint>(
                    connections.get(this));
            for (PyPoint connection : dup) {
                connection.pushState(state);
            }
        }

        /**
         * Returns the state of this point
         * 
         * @return {@link #state}
         */
        public boolean state() {
            return state;
        }

        /**
         * Returns the id of this point
         * 
         * @return {@link #id}
         */
        public int id() {
            return id;
        }

        /**
         * Returns this point as a string, including it's id, state, and
         * connection list.<br>
         * <br>
         * WARNING: If connections are two-way, this goes over the same point
         * repeatedly and causes a {@link StackOverflowError}.
         */
        @Override
        public String toString() {
            return "(point" + id + ": state:" + Conversion.getStateAsStr(state)
                    + "; conns:" + connections.get(this) + ")";
        }
    }
}
