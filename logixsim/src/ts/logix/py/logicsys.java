package ts.logix.py;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ts.logix.py.pointsys.PyPoint;

public class logicsys {

    /**
     * Indicates that a gate uses a varying amount of inputs/outputs.
     */
    public static final int VARIABLE_COUNT = -1;

    private static HashMap<String, GateInfo> gates = new HashMap<String, GateInfo>();

    private static HashMap<Class<? extends Gate>, GateInfo> classToGate = new HashMap<Class<? extends Gate>, GateInfo>();

    public static GateBuilder newGate(String id) {
        return new GateBuilder(id);
    }

    public static void addGate(Class<? extends Gate> gCls, String gId,
            int inputCount, int outputCount) {
        GateInfo gi = new GateInfo(gCls, gId, inputCount, outputCount);
        gates.put(gId, gi);
        classToGate.put(gCls, gi);
    }

    public static class GateBuilder {
        private String gid;
        private int icount = 1, ocount = 1;

        public GateBuilder(String id) {
            gid = id;
        }

        public GateBuilder setInputs(int count) {
            icount = count;
            return this;
        }

        public GateBuilder setOutputs(int count) {
            ocount = count;
            return this;
        }

        public Gate build() {
            GateInfo gi = gates.get(gid);
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

    private static class GateInfo {
        private Class<? extends Gate> gclass = null;
        private Constructor<? extends Gate> constr = null;
        private String gid = "";
        private int icount = 0, ocount = 0;

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

        @Override
        public String toString() {
            return "class: " + gclass + ", id: " + gid + ", inCount: " + icount
                    + ", outCount: " + ocount;
        }
    }

    public static abstract class Gate {
        public List<PyPoint> inputs = null, outputs = null;

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

        public void doLogic() {
            boolean res = doLogic_(inputs);
            for (PyPoint out : outputs) {
                out.pushState(res);
            }
        }

        protected abstract boolean doLogic_(List<PyPoint> inputs);

        @Override
        public String toString() {
            return classToGate.get(getClass()).gid + " [inputs: " + inputs
                    + ", outputs: " + outputs + "]";
        }
    }

    private static class AndGate extends Gate {

        protected AndGate(int ins, int outs) {
            super(ins, outs);
        }

        @Override
        protected boolean doLogic_(List<PyPoint> inputs) {
            boolean res = State.ON;
            for (PyPoint in : inputs) {
                res &= in.state;
            }
            return res;
        }
    }

    private static class OrGate extends Gate {

        protected OrGate(int ins, int outs) {
            super(ins, outs);
        }

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
        addGate(AndGate.class, "andGate", VARIABLE_COUNT, 1);
        addGate(OrGate.class, "orGate", VARIABLE_COUNT, 1);
    }
}
