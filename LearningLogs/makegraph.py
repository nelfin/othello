#! /usr/bin/python
import matplotlib.pyplot as plt
import os

os.system("cat LearningHistory_*.csv > combine.csv")
x = []
y = []
with open("combine.csv", "r") as f:
    [(x.append(i.split(",")[0]), y.append(i.split(",")[1])) for i in f]

plt.plot(x,y)
plt.show()
