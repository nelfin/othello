#! /usr/bin/python
import matplotlib.pyplot as plt
import os
os.system("cat LearningHistory_*.csv > combine.csv")
with open("combine.csv", "r") as f:
    data = [(int(i.split(",")[0]), float(i.split(",")[1])) for i in f]
data.sort()
plt.plot([i[0] for i in data],[i[1] for i in data])
plt.show()
