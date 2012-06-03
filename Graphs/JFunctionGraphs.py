#!/usr/bin/python

import numpy as np
import matplotlib.pyplot as plt
import os

with open('JFunction_Data/iteration1000_lost.csv') as f:
    data = np.loadtxt(f, dtype={
        'names': ('Move', 'J'),
        'formats': ('i', 'd')}, delimiter=',', skiprows=1)

plt.plot(data['Move'], data['J'])
plt.axis(( 1, len(data['Move']),0, 1.0))
ax = plt.axes()
ax.set_xlabel("Move")
ax.set_ylabel("J(Board)")
plt.show()
