package ts.logix.py;

import java.util.Arrays;

import ts.logix.py.logicsys.Gate;
import ts.logix.py.pointsys.PyPoint;

public class PyLogixTest {

    public static void main(String[] args) {
        PyPoint one = new PyPoint(), two = new PyPoint();
        logixmain__.connect(one, two);
        System.err.println(one.isConnected(two));
        System.err.println(one.state() + "=" + two.state());
        one.pushState(State.ON);
        System.err.println(one.state() + "=" + two.state());
        one.pushState(State.OFF);
        logixmain__.disconnect(one, two);
        Gate gate = logicsys.newGate("andGate").build();
        logixmain__
                .wireInputsToGate(Arrays.asList(new PyPoint[] { one }), gate);
        logixmain__.wireGateToOutputs(gate,
                Arrays.asList(new PyPoint[] { two }));
        System.err.println(gate);
        one.pushState(State.ON);
        gate.doLogic();
        System.err.println(gate);

    }

}
