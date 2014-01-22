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
