""" Main file for logix """
# -*- coding: utf-8 -*-
# python3
import State
import logicsys
from pointsys import Point

"""

Connects p1 to p2, but not p2 to p1
"""
def connect(p1, p2):
    if isinstance(p1,Point) and isinstance(p2, Point) :
        p1.markConnection(p2, State.Connected)

"""

Wire the given inputs to the gate's inputs
"""
def wireInputsToGate(inputs, gate) :
    for i in range(0, len(inputs)) :
        connect(inputs[i], gate.input_[i])

"""

Wire the gate's outputs to the given outputs
"""        
def wireGateToOutputs(gate, outputs) :
    for i in range(0, len(outputs)) :
        connect(gate.output[i], outputs[i])
# debug
p_1 = Point() #0
p_2 = Point() #1
p_1.pushState(State.On)
and_g=logicsys.newGate("and_gate", 2, 1) #2,3,4
p_1.markConnection(and_g.input_[0], True)#0->2
p_2.markConnection(and_g.input_[1], True)#1->3
p_3 = Point()
wireGateToOutputs(and_g, [p_3])
print(p_1)
print(p_2)
print(and_g)
print(p_3)
print("\ninsert logic here\n")
and_g.doLogic()
print(p_1)
print(p_2)
print(and_g)
print(p_3)