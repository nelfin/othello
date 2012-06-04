#!/usr/bin/python

import numpy as np
import matplotlib.pyplot as plt
import os

with open('JFunction_Data/iteration1000_dt.csv') as f:
    data = np.loadtxt(f, dtype={
        'names': ('Move', 'J'),
        'formats': ('i', 'd')}, delimiter=',', skiprows=1)

plt.plot(data['Move'], data['J'])
#plt.axis(( 1, len(data['Move']),0, 1.0))
ax = plt.axes()
ax.set_xlabel("Move")
ax.set_ylabel("J(Board)")
plt.show()
data = data['J']
s = 0
for j in data:
    s+= abs(j)
mean =  (s / len(data))
variance = 0
for j in data:
    variance+= abs(j-mean)
variance = variance / len(data)
for j in range(len(data)):
    data[j] = abs(data[j])
data.sort
mode = data[len(data)/2]
print mean," ",variance," ",mode


