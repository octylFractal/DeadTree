package ts.logix.py;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ts.logix.py.PointSys.PyPoint;

/**
 * The matching Python file is named logicsys.py.
 * 
 * @author Kenzie Togami
 */
public class GateSys {

    /**
     * Indicates that a gate uses a varying amount of inputs/outputs.
     */
    public static final int VARIABLE_COUNT = -1;

    public static final String AND_GATEID = "andGate";

    public static final String OR_GATEID = "orGate";

    /**
     * The map of ids to {@link GateInfo}.
     */
    private static HashMap<String, GateInfo> gates = new HashMap<String, GateInfo>();

    /**
     * The map of {@link Gate} classes to the respective {@link GateInfo
     * GateInfos}.
     */
    private static HashMap<Class<? extends Gate>, GateInfo> classToGate = new HashMap<Class<? extends Gate>, GateInfo>();

    /**
     * Returns a new {@link GateBuilder} to create {@link Gate Gates} with.
     * 
     * @param id
     *            - the id of the gate to create
     * @return a new GateBuilder with the gate id <tt>id</tt>.
     */
    public static GateBuilder newGate(String id) {
        return new GateBuilder(id);
    }

    /**
     * Registers this gate with the internal system.
     * 
     * @param gCls
     *            - the gate's class
     * @param gId
     *            - the gate's identifier
     * @param inputCount
     *            - the number of inputs this accepts, or
     *            {@link GateSys#VARIABLE_COUNT} for any number of inputs.
     * @param outputCount
     *            - the number of outputs this accepts, or
     *            {@link GateSys#VARIABLE_COUNT} for any number of outputs.
     */
    public static void addGate(Class<? extends Gate> gCls, String gId,
            int inputCount, int outputCount) {
        GateInfo gi = new GateInfo(gCls, gId, inputCount, outputCount);
        gates.put(gId, gi);
        classToGate.put(gCls, gi);
    }

    /**
     * A class for building {@link Gate Gates}.
     * 
     * @author Kenzie Togami
     */
    public static class GateBuilder {
        /**
         * The id for the gate.
         */
        private String gid;
        /**
         * The number of inputs/outputs used by the created gate.
         */
        private int icount = 1, ocount = 1;

        /**
         * Creates a new GateBuilder with the specified id.
         * 
         * @param id
         *            - the gate id
         */
        public GateBuilder(String id) {
            gid = id;
        }

        /**
         * Sets the number of inputs to create the gate with.
         * 
         * @param count
         *            - the number of inputs
         * @return <tt>this</tt>
         */
        public GateBuilder setInputs(int count) {
            icount = count;
            return this;
        }

        /**
         * Sets the number of outputs to create the gate with.
         * 
         * @param count
         *            - the number of outputs
         * @return <tt>this</tt>
         */
        public GateBuilder setOutputs(int count) {
            ocount = count;
            return this;
        }

        /**
         * Constructs the {@link Gate} and performs checks.
         * 
         * @return a new {@link Gate}
         */
        public Gate build() {
            GateInfo gi = gates.get(gid);
            if (icount < 1 || ocount < 1) {
                throw new IndexOutOfBoundsException(
                        "input/output count must be greater than 0");
            }
            if (gi.icount != VARIABLE_COUNT && icount != gi.icount) {
                throw new IllegalStateException("Input counts do not match.");
            }
            if (gi.ocount != VARIABLE_COUNT && ocount != gi.ocount) {
                throw new IllegalStateException("Input counts do not match.");
            }
            try {
                return gi.constr.newInstance(icount, ocount);
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Holds info about a {@link Gate} class.
     * 
     * @author Kenzie Togami
     */
    private static class GateInfo {
        /**
         * The associated {@link Class}.
         */
        private Class<? extends Gate> gclass = null;
        /**
         * The constructor used to create a {@link Gate}.
         */
        private Constructor<? extends Gate> constr = null;
        /**
         * The id of the {@link Gate}.
         */
        private String gid = "";
        /**
         * The allowed inputs/outputs for this gate class.
         */
        private int icount = 0, ocount = 0;

        /**
         * Creates a new GateInfo.
         * 
         * @param gCls
         *            - the class for the gate
         * @param id
         *            - the id for the gate
         * @param inputs
         *            - the number of inputs the gate accepts, or
         *            {@link GateSys#VARIABLE_COUNT} for any number of inputs.
         * @param outputs
         *            - the number of outputs the gate accepts, or
         *            {@link GateSys#VARIABLE_COUNT} for any number of outputs.
         */
        private GateInfo(Class<? extends Gate> gCls, String id, int inputs,
                int outputs) {
            gclass = gCls;
            try {
                constr = gCls.getDeclaredConstructor(int.class, int.class);
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException(e);
            }
            gid = id;
            icount = inputs;
            ocount = outputs;
        }

        /**
         * Returns a textual representation of this {@link GateInfo}.
         */
        @Override
        public String toString() {
            return "class: " + gclass + ", id: " + gid + ", inCount: " + icount
                    + ", outCount: " + ocount;
        }
    }

    /**
     * An abstract class that provides the basis of a Gate.
     * 
     * @author Kenzie Togami
     */
    public static abstract class Gate {
        /**
         * The inputs/outputs in this Gate.
         */
        public List<PyPoint> inputs = null, outputs = null;

        /**
         * Creates a new {@link Gate} with the specified number of ins and outs.
         * 
         * @param ins
         *            - the number of input points
         * @param outs
         *            - the number of output points.
         */
        protected Gate(int ins, int outs) {
            inputs = new ArrayList<PyPoint>(ins);
            outputs = new ArrayList<PyPoint>(outs);
            for (int i = 0; i < ins; i++) {
                inputs.add(new PyPoint());
            }
            for (int i = 0; i < outs; i++) {
                outputs.add(new PyPoint());
            }
        }

        /**
         * Performs the logic this gate does, using the input list and pushing
         * the result to the output list.
         */
        public void doLogic() {
            boolean res = doLogic_(inputs);
            for (PyPoint out : outputs) {
                out.pushState(res);
            }
        }

        /**
         * Performs the logic this gate does on the given input list, and
         * returns the boolean result.
         * 
         * @param inputs
         *            - a list of {@link PyPoint PyPoints} to use as input.
         * @return the result of the logic being preformed on the input list.
         */
        protected abstract boolean doLogic_(List<PyPoint> inputs);

        /**
         * Returns a textual representation of this {@link Gate}.
         */
        @Override
        public String toString() {
            return classToGate.get(getClass()).gid + " [inputs: " + inputs
                    + ", outputs: " + outputs + "]";
        }
    }

    /**
     * The implementation of an and gate.
     * 
     * @author Kenzie Togami
     */
    private static class AndGate extends Gate {

        /**
         * @see Gate#Gate(int, int)
         */
        protected AndGate(int ins, int outs) {
            super(ins, outs);
        }

        /**
         * This does an <i>and</i> over the list.<br>
         * <br>
         * {@inheritDoc}
         */
        @Override
        protected boolean doLogic_(List<PyPoint> inputs) {
            boolean res = State.ON;
            for (PyPoint in : inputs) {
                res &= in.state;
            }
            return res;
        }
    }

    /**
     * The implementation of an or gate.
     * 
     * @author Kenzie Togami
     */
    private static class OrGate extends Gate {

        /**
         * @see Gate#Gate(int, int)
         */
        protected OrGate(int ins, int outs) {
            super(ins, outs);
        }

        /**
         * This does an <i>or</i> over the list.<br>
         * <br>
         * {@inheritDoc}
         */
        @Override
        protected boolean doLogic_(List<PyPoint> inputs) {
            boolean res = State.OFF;
            for (PyPoint in : inputs) {
                res |= in.state;
            }
            return res;
        }
    }

    static {
        // this adds the default gates.
        addGate(AndGate.class, AND_GATEID, VARIABLE_COUNT, 1);
        addGate(OrGate.class, OR_GATEID, VARIABLE_COUNT, 1);
    }
}
