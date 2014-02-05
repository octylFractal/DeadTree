package ts.deadtree.positionables;

import ts.deadtree.py.PointSys.PyPoint;

public class PPyPoint extends Positionable {
    public PyPoint point = null;

    public PPyPoint() {
        point = new PyPoint();
    }
}
