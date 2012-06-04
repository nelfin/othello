#!/usr/bin/python

import numpy as np
import matplotlib.pyplot as plt
import os
import matplotlib.font_manager as fm

with open('Weights_Over_Time_20.csv') as f:
    data = np.loadtxt(f, dtype={
        'names': ('LegalMoves', 'Stone Count', 'BlockedAdjacent', 'SidePieces','CornerPieces'),
        'formats': ('d', 'd', 'd','d','d')}, delimiter=',', skiprows=1)
        
from scipy.interpolate import spline

xnew = np.linspace(1,20,220)

lm_smooth = spline(range(1,20),data['LegalMoves'],xnew)
sc_smooth = spline(range(1,20),data['Stone Count'],xnew)
ba_smooth = spline(range(1,20),data['BlockedAdjacent'],xnew)
sp_smooth = spline(range(1,20),data['SidePieces'],xnew)
cp_smooth = spline(range(1,20),data['CornerPieces'],xnew)

plt.plot(lm_smooth)
plt.plot(sc_smooth)
plt.plot(ba_smooth)
plt.plot(sp_smooth)
plt.plot(cp_smooth)
plt.axis(( 1, 200,0, 0.5))
plt.xticks([0,50,100,150,200],['0','5','10','15','20'])
prop = fm.FontProperties(size=11)
l = plt.legend(('Legal Moves', 'Stone Count', 'Blocked Adjacent', 'SidePieces', 'CornerPieces'),prop=prop)
plt.show()
