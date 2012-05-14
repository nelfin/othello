#!/usr/bin/python

import numpy as np
import matplotlib.pyplot as plt
import os

os.system("cat LearningHistory*.csv > combine.csv")

with open('combine.csv') as f:
    data = np.loadtxt(f, dtype={
        'names': ('iteration', 'cumAvg', 'expMovAvg'),
        'formats': ('i', 'd', 'd')}, delimiter=',', skiprows=1)

plt.plot(data['iteration'], data['cumAvg'])
plt.plot(data['iteration'], data['expMovAvg'])
plt.show()
