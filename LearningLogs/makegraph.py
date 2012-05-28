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
#plt.axis((0, 5000, 0.2, 0.9))
ax = plt.axes()
ax.set_xlabel("Learning iteration")
ax.set_ylabel("Win / Loss")
leg = ax.legend(('Running average', 'Exponential Moving average'),
           'best', shadow=True)
plt.show()
