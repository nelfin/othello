#! /usr/bin/python

import numpy as np
import matplotlib.pyplot as plt
from ast import literal_eval
import os

plt.ion()

data = open('nstagedata').read()
weights = data.split("|||")[-1]
LM, SC, SP, CP, BA = [], [], [], [], []
meta, weights = weights.split("||")
colour, i = meta.split("|")
odd = 0
for line in weights.split("\n"):
    if odd == 1:
        odd = 0
        #continue  #Smooth out parity
    if not line: continue
    line = literal_eval(line[line.index("{"):])
    LM.append(line["LegalMoves"])
    SC.append(line["StoneCount"])
    SP.append(line["SidePieces"])
    CP.append(line["CornerPieces"])
    BA.append(line["BlockedAdjacent"])
    odd = 1

plt.plot(range(1,len(LM)+1), LM, label='LegalMoves')
plt.plot(range(1,len(SC)+1), SC, label='StoneCount')
plt.plot(range(1,len(SP)+1), SP, label='SidePieces')
plt.plot(range(1,len(CP)+1), CP, label='CornerPieces')
plt.plot(range(1,len(BA)+1), BA, label='BlockedAdjacent')
plt.axis([1, 90, 0, 1])
plt.legend()
ax = plt.axes()
ax.set_xlabel("Move")
ax.set_ylabel("Feature Weight")
#plt.show()

raw_input("Press enter for next graph")
plt.clf()

LM = {}
for weights in data.split("|||"):
    if not weights: continue
    meta, weights = weights.split("||")
    colour, i = meta.split("|")
    i = int(i)
    if colour == " BLACK ": continue
    if i < 1000 and (i != 1 and i != 10 and i != 100 and i != 500): continue
    CLM = []
    for line in weights.split("\n"):
        if not line: continue
        line = literal_eval(line[line.index("{"):])
        CLM.append(line["LegalMoves"])
    LM[i] = CLM

iters = LM.keys()
iters.sort()
for i in iters:
    plt.plot(range(1,len(LM[i])+1), LM[i], label=str(i))
plt.axis([1, 90, 0, 1])
plt.legend()
ax = plt.axes()
ax.set_xlabel("Move")
ax.set_ylabel("LegalMoves Weight")
plt.show()
