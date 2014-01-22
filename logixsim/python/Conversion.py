""" A place to convert things """
       
import pointsys

def getPointValues(points) :
    if isinstance(points, pointsys.Point) :
        return points.state
    ret = []
    for point in points :
        ret.append(point.state)
    return ret

def getStateAsStr(p) :
    if p.state :
        return "On"
    return "Off"
