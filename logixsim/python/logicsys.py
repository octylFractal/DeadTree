""" The logic gate systematics """

import Conversion
from State import On,Off
from pointsys import Point

gates = dict()
rev_gate_dict = dict()

""" Stands for a variable amt of inputs """
VARIABLE = -1

def newGate(new_gate_id, inputCount=1, outputCount=1) :
    info = gates[new_gate_id]
    if info is None :
        raise LookupError("No gate info")
    if info.inp is not VARIABLE and info.inp is not inputCount :
        raise ValueError("Input count does not match")
    if info.out is not VARIABLE and info.out is not outputCount :
        raise ValueError("Output count does not match")
    return info.gate_c(inputCount, outputCount)

def addGate(gate_class, gate_id, inputCount=1, outputCount=1) :
    gates[gate_id] = GateInfo(gate_class, gate_id, inputCount, outputCount)
    rev_gate_dict[gate_class] = gates[gate_id]


class GateInfo() :
    def __init__(self, gCls, gID, inputs, outputs) :
        self.gate_c = gCls
        self.gate = gID
        self.inp = inputs
        self.out = outputs
    def __repr__(self) :
        return "class: {}, id: {}, inCount: {}, outCount: {}".format(
            self.gate_c, self.gate, self.inp, self.out)
class Gate() :
    def __init__(self, ins, outs) :
        print("warning: default gate is not supported")

    def setInput(self, count) :
        self.input_ = [None]*count
        for i in range(count) :
            self.input_[i] = Point()

    def setOutput(self, count):
        self.output = [None]*count
        for i in range(count) :
            self.output[i] = Point()

    def doLogic_(self, inputValues) :
        print("Unsupported operation: Gate is abstract")
        raise NotImplementedError()

    def doLogic(self) :
        output_ = self.doLogic_(Conversion.getPointValues(self.input_))
        for out in self.output :
            out.pushState(output_)
            
    def getClass(self) :
        return self.__class__
            
    def __repr__(self) :
        return "{} [inputs: {}, outputs: {}]".format(rev_gate_dict[self.getClass()].gate, self.input_, self.output)

class AndGate(Gate) :
    def __init__(self, ins, outs) :
        self.setInput(ins)
        self.setOutput(outs)

    def doLogic_(self, inputValues) :
        out = On
        for input_ in inputValues :
            out = out and input_
        return out

class OrGate(Gate) :
    def __init__(self, ins, outs) :
        self.setInput(ins)
        self.setOutput(outs)

    def doLogic_(self, inputValues) :
        out = Off
        for input_ in inputValues :
            out = out or input_
        return out

addGate(OrGate, "or_gate", VARIABLE, 1)
addGate(AndGate, "and_gate", VARIABLE, 1)
