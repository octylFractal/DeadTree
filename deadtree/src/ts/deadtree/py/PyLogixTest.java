package ts.deadtree.py;

import java.util.Arrays;

import ts.deadtree.py.GateSys.Gate;
import ts.deadtree.py.PointSys.PyPoint;

public class PyLogixTest {

    public static void main(String[] args) {
        PyPoint one = new PyPoint(), two = new PyPoint();
        LogixUtil.connect(one, two);
        System.err.println(one.isConnected(two));
        System.err.println(one.state() + "=" + two.state());
        one.pushState(State.ON);
        System.err.println(one.state() + "=" + two.state());
        one.pushState(State.OFF);
        LogixUtil.disconnect(one, two);
        Gate gate = GateSys.newGate("andGate").build();
        LogixUtil
                .wireInputsToGate(Arrays.asList(new PyPoint[] { one }), gate);
        LogixUtil.wireGateToOutputs(gate,
                Arrays.asList(new PyPoint[] { two }));
        System.err.println(gate);
        one.pushState(State.ON);
        gate.doLogic();
        System.err.println(gate);

    }

}
