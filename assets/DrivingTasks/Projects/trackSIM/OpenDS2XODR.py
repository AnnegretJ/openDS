#!/usr/bin/env/ python

# parser to transform road file into xodr
# length, initC, endC
road = [[100.0,  0.,     0.],
        [3.14/0.05,  0.05,    0.05],
        [130.0,  0.,    0.],
        [20,    0.1,  0.1],
        [45,   -0.05, -0.05],
        [10,   -0.05,   0.],
        [5,     0.,    0.1],
        [67,    0.1,    0],
        [60,    0.,     0.]
    ]

end = """     </planView>
        <elevationProfile>
            <elevation s="0.0000000000000000e+00" a="9.5000000000000000e+00" b="0.0000000000000000e+00" c="0.0000000000000000e+00" d="0.0000000000000000e+00"/>
        </elevationProfile>
        <lateralProfile>
        </lateralProfile>
        <lanes>
            <laneSection s="0.0000000000000000e+00">
                <left>
                    <lane id="2" type="border" level= "false">
                        <link>
							<predecessor id="2"/>
							<successor id="2"/>
                        </link>
                        <width sOffset="0.0000000000000000e+00" a="1.5000000000000000e+00" b="0.0000000000000000e+00" c="0.0000000000000000e+00" d="0.0000000000000000e+00"/>
                        <roadMark sOffset="0.0000000000000000e+00" type="none" weight="standard" color="standard" width="1.3000000000000000e-01"/>
                    </lane>
                    <lane id="1" type="driving" level= "false">
                        <link>
							<predecessor id="1"/>
							<successor id="1"/>
                        </link>
                        <width sOffset="0.0000000000000000e+00" a="3.0" b="0.0000000000000000e+00" c="0.0000000000000000e+00" d="0.0000000000000000e+00"/>
                        <roadMark sOffset="0.0000000000000000e+00" type="solid" weight="standard" color="standard" width="1.3000000000000000e-01"/>
                        <speed sOffset="0.0000000000000000e+00" max="70.0" unit="km/h" />
                    </lane>
                </left>
                <center>
                    <lane id="0" type="driving" level= "false">
                        <link>
                        </link>
                        <roadMark sOffset="0.0000000000000000e+00" type="broken" weight="standard" color="standard" width="1.3000000000000000e-01"/>
                    </lane>
                </center>
                <right>
                    <lane id="-1" type="driving" level= "false">
                        <link>
							<predecessor id="-1"/>
							<successor id="-1"/>
                        </link>
                        <width sOffset="0.0000000000000000e+00" a="3.0" b="0.0000000000000000e+00" c="0.0000000000000000e+00" d="0.0000000000000000e+00"/>
                        <roadMark sOffset="0.0000000000000000e+00" type="solid" weight="standard" color="standard" width="1.3000000000000000e-01"/>
                        <speed sOffset="0.0000000000000000e+00" max="70.0" unit="km/h" />
                    </lane>
                    <lane id="-2" type="border" level= "false">
                        <link>
							<predecessor id="-2"/>
							<successor id="-2"/>
                        </link>
                        <width sOffset="0.0000000000000000e+00" a="1.5000000000000000e+00" b="0.0000000000000000e+00" c="0.0000000000000000e+00" d="0.0000000000000000e+00"/>
                        <roadMark sOffset="0.0000000000000000e+00" type="none" weight="standard" color="standard" width="1.3000000000000000e-01"/>
                    </lane>
                </right>
            </laneSection>
        </lanes>
        <objects>
        </objects>
        <signals>
        </signals>
    </road>

</OpenDRIVE>
"""

def deltaX(s, c0, gamma, A):
    return np.cos(c0 + gamma*s**2/(2*A**2))

def deltaY(s, c0, gamma, A):
    return np.sin(c0 + gamma*s**2/(2*A**2))

def rotateRF(dx,dy,hdg):
    x1=(dx*math.cos(hdg)-dy*math.sin(hdg))
    y1=(dx*math.sin(hdg)+dy*math.cos(hdg))
    return x1, y1

def reverseRotateRF(dx,dy,hdg):
    x1=(dx*math.cos(hdg)+dy*math.sin(hdg))
    y1=(-dx*math.sin(hdg)+dy*math.cos(hdg))
    return x1, -y1

def writeStraight():
    straight="""               <line/>
                </geometry>
        """
    return straight

def writeCurve(c):
    curve="""              <arc curvature=\"%f\"/>
			       </geometry>
            """%c
    return curve

def writeClothoid(initC, endC):
    clothoid="""              <spiral curvStart=\"%f\" curvEnd=\"%f\"/>
			       </geometry>
    """ % (initC, endC)
    return clothoid

def evalCurve(r, theta):
    dx = r*math.sin(theta)
    dy = r*(1-math.cos(theta))
    return dx, dy

def parseRoad():
    global sInit
    global plotStep

    # initial straight segment
    path="""
                <geometry s="0.0" x="0.0" y="0.0" hdg="0.0" length="%s">
		            	<line/>
		        </geometry>
    """% sInit

    x = sInit; s = sInit
    y = 0; hdg = 0

    xPlot=[]; yPlot=[]
    xPlot.append(0); yPlot.append(0)

    # loop over table lines
    for line in road:
        l=line[0]; initC=line[1]; endC=line[2]

        path+="""
  	            <geometry s=\"%f\" x=\"%f\" y=\"%f\" hdg=\"%f\" length=\"%f\">
        """ % (s, x, y, hdg, l)

        if(initC==0 and endC==0):
            # straight segment
            path+=writeStraight()
            dx=l; dy=0; theta=0
            x1, y1 = rotateRF(dx,dy,hdg)
            xPlot.append(x+x1); yPlot.append(y+y1)
        else:
            if (initC==endC):
                # curve segment
                c = initC
                path+=writeCurve(c)
                r = 1/c; theta = l*c
                for ll in np.arange(0,l,plotStep):
                    dx, dy = evalCurve(r, ll*c)
                    x1, y1 = rotateRF(dx,dy,hdg)
                    xPlot.append(x+x1); yPlot.append(y+y1)
                dx, dy = evalCurve(r, l*c)
                x1, y1 = rotateRF(dx,dy,hdg)
                xPlot.append(x+x1); yPlot.append(y+y1)
                hdg+=theta
            else:
                path+=writeClothoid(initC, endC)
                if(endC!=0):
                    gamma = np.sign(endC)
                    A = math.sqrt(l/abs(endC))
                    theta = gamma*l**2/(2*A**2)
                    for ll in np.arange(0,l,plotStep):
                        dx = integrate.quad(deltaX, 0, ll, args=(initC, gamma, A))[0]
                        dy = integrate.quad(deltaY, 0, ll, args=(initC, gamma, A))[0]
                        x1, y1 = rotateRF(dx,dy,hdg)
                        xPlot.append(x1+x); yPlot.append(y1+y)
                    theta = gamma*l**2/(2*A**2)
                    dx = integrate.quad(deltaX, 0, l, args=(0, gamma, A))[0]
                    dy = integrate.quad(deltaY, 0, l, args=(0, gamma, A))[0]
                    x1, y1 = rotateRF(dx,dy,hdg)
                    xPlot.append(x+x1); yPlot.append(y+y1)
                    hdg+=theta
                else:
                    gamma = np.sign(initC)
                    A = math.sqrt(l/abs(initC))
                    for ll in np.arange(0,l,plotStep):
                        theta = gamma*ll**2/(2*A**2)
                        dx = integrate.quad(deltaX, 0, ll, args=(0, gamma, A))[0]
                        dy = integrate.quad(deltaY, 0, ll, args=(0, gamma, A))[0]
                        x1, y1 = reverseRotateRF(dx,dy,hdg+theta)
                        xPlot.append(x+x1); yPlot.append(y+y1)
                    theta = gamma*l**2/(2*A**2)
                    dx = integrate.quad(deltaX, 0, l, args=(0, gamma, A))[0]
                    dy = integrate.quad(deltaY, 0, l, args=(0, gamma, A))[0]
                    x1, y1 = reverseRotateRF(dx,dy,hdg+theta)
                    xPlot.append(x+x1); yPlot.append(y+y1)
                    hdg+=theta
        x+=x1; y+=y1
        s+=l
    
    header = """<?xml version="1.0" standalone="yes"?>
<OpenDRIVE>
    <header revMajor="1" revMinor="1" name="%s" version="1.00" date="%s" north="1.9000000000000000e+03" south="-1.1500000000000000e+03" east="3.3000000000000000e+03" west="-4.8000000000000000e+02">
    </header>
    <road name="s000" length="%f" id="1" junction="-1">
        <link>

	
        </link>
        <planView>
""" % ("D4C_XODR_generator", datetime.now().strftime("%y-%m-%d-%H-%M"), s)
    path+=end

    roadFile = open("opends1.xodr","w")
    roadFile.write("%s" % header)
    roadFile.write("%s" % path)
    roadFile.close()

    print(" --------------- road file generated ---------------- ")

    fig = plt.figure()
    plt.plot(xPlot,yPlot,'-*')
    plt.xlabel('x [m]'); plt.xlabel('y [m]')
    plt.axis('equal')
    fig.savefig('road.png')
    #plt.show(block=False)
    plt.draw()
    plt.pause(0.001)
    input("Press [enter] to continue.")

if __name__ == "__main__":
    import sys
    import random
    import numpy as np
    import math
    from datetime import datetime
    import scipy.integrate as integrate
    import matplotlib.pyplot as plt

    sInit = 1
    plotStep = 1

    parseRoad()
