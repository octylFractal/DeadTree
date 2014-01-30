package ts.logix.positionables;

import ts.logix.py.PointSys.PyPoint;

public class PPyPoint extends Positionable {
    public PyPoint point = null;

    public PPyPoint() {
        point = new PyPoint();
    }
}
