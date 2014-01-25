package ts.logix.py;

import ts.logix.py.pointsys.PyPoint;

public class PyLogixTest {

    public static void main(String[] args) {
        PyPoint one = new PyPoint(), two = new PyPoint();
        logixmain__.connect(one, two);
        System.err.println(one.isConnected(two));
        System.err.println(one.state() + "=" + two.state());
        one.pushState(State.ON);
        System.err.println(one.state() + "=" + two.state());
    }

}
