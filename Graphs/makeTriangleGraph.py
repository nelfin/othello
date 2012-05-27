#! /usr/bin/python
import matplotlib.pyplot as plt

up = (0,1.0/((3**0.5)))
botLeft = (-1.0/2.0,-1.0/2 * (3**0.5))
botRight = (1.0/2.0,-1.0/2 * (3**0.5))

def triangleToCartesian(x,y,z):

    
    return (up[0]*x + botLeft[0]*y + botRight[0]*z,up[1]*x + botLeft[1]*y + botRight[1]*z)

from matplotlib import pyplot as PLT
from matplotlib import cm as CM
from matplotlib import mlab as ML
import numpy as NP

x = list()
y = list()
z = list()
with open("triangle.csv", "r") as f:
    data = [(float(i.split(",")[0]), float(i.split(",")[1]),float(i.split(",")[2]), float(i.split(",")[3])) for i in f]
for i in range(len(data)):
    (pointX, pointY) = triangleToCartesian(data[i][0],data[i][1],data[i][2])
    x.append(pointX)
    y.append(pointY)
    z.append(data[i][3])
    
#xArray = NP.array(x)
#yArray = NP.array(y)
#zArray = NP.array(z)
#X,Y = NP.meshgrid(xArray,yArray)
#PLT.pcolor(X, Y, zArray)
#PLT.colorbar()
#PLT.axis([-1,1,-1,1])

#PLT.show()


gridsize=50
PLT.subplot(111)
# if "bins=None", then color of each hexagon corresponds directly to its count
# "C" is optional--it maps values to x, y coordinates; if C is None (default) then 
# the result is a pure 2D histogram 
PLT.hexbin(x, y, C=z, gridsize=gridsize, cmap=CM.jet, bins=None)
PLT.axis((-1, 1, -1, 1))
ax = PLT.axes()

PLT.text(up[0],up[1]+0.1, "Stone Count",
     horizontalalignment='center',
     verticalalignment='center') 
PLT.text(botLeft[0],botLeft[1]-0.05, "Legal Moves",
     horizontalalignment='center',
     verticalalignment='center')
PLT.text(botRight[0],botRight[1]-0.05, "Side Pieces",
     horizontalalignment='center',
     verticalalignment='center')

cb = PLT.colorbar()
cb.set_label('Win / Loss')
PLT.show()   
