#! /usr/bin/python
from matplotlib import pyplot as PLT
from matplotlib import cm as CM
from matplotlib import mlab as ML
import numpy as NP
names = ('Legal Moves','Stone Count','Visibility','Side Pieces','Corner Pieces','Blocked Adjacent')
ind = NP.arange(6)
valuesW = (0.16624584609586937,0.05970565007068347,0.03477484713452541,0.2812365145154657,0.24368416503769144,0.21435297714576476)
valuesB = (0.16233859309382384,0.007436043723223994,0.003858647010800425,0.328273658910231,0.2773790640278619,0.22071399323405883)
PLT.ylabel('Value % Difference (White - Black)')
ax = PLT.axes()
PLT.bar(ind,[m - n for m,n in zip(valuesW,valuesB)])
PLT.xticks(ind+0.5/2, names )
for tick in ax.xaxis.get_major_ticks():
    tick.label1.set_fontsize(14)
    tick.label1.set_rotation(12)

PLT.show()   
