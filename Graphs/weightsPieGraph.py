#! /usr/bin/python
from matplotlib import pyplot as PLT
from matplotlib import cm as CM
from matplotlib import mlab as ML
import numpy as NP
width=0.2
names = ('Legal Moves','Stone Count','Blocked Adjacent','Side Pieces','Corner Pieces')
ind = NP.arange(5)
valuesStart = (0.8733483528165487,0.003078822843581382,0.10011922656344638,0.014493983064398804,  0.008959614712024862)
valuesMid = (0.33323454818417325,0.02401178293137336,0.20900258701675864,0.24816610013309515, 0.18558498173459956)
valuesLate = ( 0.08990255340827673,0.05584277714794637,0.24475285222360674,0.34081897757794566, 0.2686828396422245)
PLT.ylabel('Value %')
ax = PLT.axes()
b1=PLT.bar(ind,valuesStart,width,color="red")
b2=PLT.bar(ind+width,valuesMid,width,color="green")
b3=PLT.bar(ind+width*2,valuesLate,width,color="blue")
PLT.xticks(ind+width/2, names )
leg = ax.legend((b1[0],b2[0],b3[0]),('Early game', 'Mid game', 'Late game'),
           'best', shadow=True)
for tick in ax.xaxis.get_major_ticks():
    tick.label1.set_fontsize(14)
    tick.label1.set_rotation(12)

PLT.show()   
