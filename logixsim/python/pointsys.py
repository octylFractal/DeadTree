""" The connections/point systematics """

import Conversion
from State import Off, On, Connected, Unconnected

""" Stores connections in point->list-of-point pairs """
connections = {}

nextID = 0

def writeConnection(point, connList) :
    connections[point] = connList

def addConnection(point, point2) :
    if point2 not in connections[point] :
        connections[point].append(point2)
        point2.pushState(point.state)
    else :
        print("Skipping point {}: already connected.".format(point2))

def remConnection(point, point2) :
    connections[point].remove(point2)

class Point():

    def __init__(self) :
        global nextID
        self.state = Off
        self.id = nextID
        nextID += 1
        writeConnection(self, [])

    def __repr__(self) :
        return "(point{}: state:{}; conns:{})".format(self.id, Conversion.getStateAsStr(self), connections[self])

    """

    Makes or removes a connection between this point and p2
    """
    def markConnection(self, p2, connected) :
        if connected :
            addConnection(self, p2)
        elif not connected :
            remConnection(self, p2)
    """

    Returns if this point is connected to p2
    """
    def isConnected(self, p2) :
        return p2 in connections[self]

    """

    Sets the state of this point and it's connections
    """
    def pushState(self, state) :
        self.state = state
        # self-less thread protection
        for connection in connections[self][:] :
            connection.pushState(state)
