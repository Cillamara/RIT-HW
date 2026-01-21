"""
file: quilt_design.py
description: a program that draws quilt designs using the turtle library.
language: python3
Author: Liam Cui, lc4275@g.rit.edu
"""

import turtle as t
import math as m

SIZE = 200
"""Length of each block in pixels"""

INNER = SIZE * 0.1 
"""Length of inner block offset in pixels"""

MHYPOT = SIZE * 0.25 
"""Length of hypotenuse of the maroon triangle from first pattern"""

MSIDE = PHYPOT = BLUEEDGE = MHYPOT * m.sin(m.pi * 0.25)
"""
Length of the legs of the maroon triangle from the first pattern, length of 
the sides of the dark blue square from the second pattern, length of the 
hypotenuse of the purple triangle in the first pattern
"""

PSIDE = TSIDE = PHYPOT * m.sin(m.pi * 0.25)
"""
Length of the legs of the purple triangle from the first pattern, length of 
the legs of the triangles from the second pattern
"""

RADIUS = (MHYPOT * 0.75)
"""
Height of each ray from pattern 3, shortest distance from the center to 
the base of each ray
"""

RAYBASE = 2 * RADIUS * m.sin(m.radians(10))
"""
Base length of each ray from pattern 3, side length of each surface triangle 
from pattern 3, length of each concavity in the suncenter drawing of pattern 3
"""

RAYSIDE = RADIUS * m.cos(m.radians(10))
"""Length of each leg of the rays from pattern 3"""

def TurtleGoFast():
    """
    Increases turtle speed

    :param speed: 100
    """
    t.speed(100)

def StartingPos(SIZE):
    """
    Deterines starting postion for pattern block creation so that when the 
    blocks are made they will be vertically and horizontally centered in a 
    horizontal row configuration.

    :pre: turtle facing east at origin, pen up or down
    :post: turtle facing east 1.6 * block lengths left of origin 
        and .5 block lengths below origin, pen up
    """
    t.penup()
    #1.5 block lengths from left of center plus .1 block length gap
    t.left(180)
    t.forward(SIZE * 1.6)
    #vertically center pattern blocks
    t.left(90)
    t.forward(SIZE * 0.5)
    t.left(90)

def drawsquare(SIZE):
    """
    Draws outer square of the block that encapsulates the pattern
    
    :param size: 200 * 200 pixel dimension square
    :pre: starts at a corner of the square, turtle facing the center of an edge, 
        pen down
    :post: -"-
    """
    for i in range(4):
        t.forward(SIZE)
        t.left(90)

def drawinnersquare(SIZE, INNER):
    """
    Draws the inner portion of the block that encapsulates the pattern
    to be called upon by drawblock()
    
    :param size: a 160 pixel length sided square with 20 pixel 
        outward protrusions along each side of each edge
    :pre: starts at a corner of the square, turtle facing the center of an edge, 
        pen down
    :post: -"-
    """
    t.forward(INNER)
    t.left(90)
    t.forward(SIZE)
    for i in range(3):
        for i in range(2):
            t.left(90)
            t.forward(INNER)
        t.left(90)
        t.forward(SIZE)
    t.left(90)
    t.forward(INNER)
    t.left(90)


def drawblock(SIZE, INNER):
    """
    Draws the block used to encapsulate a pattern by calling upon 
    drawsquare() and draw innersquare().
    
    :param SIZE: 200 x 200 pixel area
    :pre: starts at bottom left corner, turtle facing east, 
        square facing up, pen up
    :post: -"-
    """
    t.pendown()
    drawsquare(SIZE)
    drawinnersquare(SIZE, INNER)
    t.penup()


def MoveCenter(SIZE):
    """
    Moves Turtle position from bottom left corner to the center of each block

    :pre: Starts at bottom left corner of a block, turtle facing east, 
        square facing up, pen up
    :post: Center of block, turtle facing north, pen up
    """
    t.forward(SIZE * 0.5)
    t.left(90)
    t.forward(SIZE * 0.5)

def MoveNext(SIZE):
    """
    Moves turtle from center of each block to the starting position of the 
    next block

    :pre: Center of a Block, turtle facing north, pen up
    :post: Bottom left corner of the next block, turtle facing east, pen up
    """
    t.left(180)
    t.forward(SIZE * 0.5)
    t.left(90)
    t.forward(SIZE * 0.6)
    
def drawMtriangle(MHYPOT, MSIDE):
    """
    Draws the larger maroon triangle to be called by drawpattern1()
    
    :param size: 50, 50sin(45), 50sin(45) pixel side length triangle
    :param fillcolor: maroon
    :pre: corner clockwise of the base, facing the opposite 
        base angle, pen down
    :post: corner clockwise the base, facing 180 degrees away
        from the vertex angle, pen down
    """
    t.fillcolor("maroon")
    t.begin_fill()
    t.forward(MHYPOT)
    t.left(135)
    t.forward(MSIDE)
    t.left(90)
    t.forward(MSIDE)
    t.end_fill()
    
def drawPtriangle(PHYPOT, PSIDE):
    """
    Draws the smaller purple triangle to be called by drawpattern1().
    
    :param size: 50sin(45), 50sin2(45), 50sin2(45) pixel side length triangle
    :param fillcolor: purple
    :pre: corner clockwise of the base,
        facing the opposite base angle, pen down
    :post: corner clockwise the base, facing 180 degrees away
        from the vertex angle, pen down
    """
    t.fillcolor("purple")
    t.begin_fill()
    t.forward(PHYPOT)
    t.left(135)
    t.forward(PSIDE)
    t.left(90)
    t.forward(PSIDE)
    t.end_fill()

def drawpattern1(MHYPOT, MSIDE, PHYPOT, PSIDE):
    """
    Draws and colors in a pattern. First it calls drawMtriangle() to draw a 
    Maroon triangle from the center with its hypotenuse along an imaginary 
    vertical axis center of the block. Then it calls drawPtriangle() to draw a 
    smaller purple triangle from the center with a leg on the vertical axis 
    but flipped from an imaginary perpendicular axis at the center. These 
    drawings are then repeated for y = x axis, horizontal axis, and y=-x 
    axis.

    :param maroon triangle size: 50, 50sin(45), 50sin(45) pixel side lengths
    :param purple triangle size: 50sin(45), 50sin2(45), 50sin2(45) pixel 
        side lengths
    :pre: Center of the left most block, turtle facing north, pen up
    :post: -"-
    """
    t.pendown()
    for i in range(4):
        drawMtriangle(MHYPOT, MSIDE)
        drawPtriangle(PHYPOT, PSIDE)
    t.penup()

def drawDBsquare(BLUEEDGE):
    """
    Draws a dark blue square to be called upon by draw pattern2().

    :param size: 50sin(45) * 50sin(45) pixel square
    :param fillcolor: dark blue
    :pre: Center of the middle block, turtle facing north, pen up
    :post: Top left corner of the new square, turtle facing west,
        square facing down, pen down
    """
    t.penup()
    t.forward(BLUEEDGE * 0.5)
    t.left(90)
    t.forward(BLUEEDGE * 0.5)
    t.pendown()
    t.fillcolor("darkblue")
    t.begin_fill()
    for i in range(4):
        t.left(90)
        t.forward(BLUEEDGE)
    t.end_fill()

def drawtriangle(BLUEEDGE, TSIDE):
    """
    Draws the triangle shape to be called upon by drawthreetriangles().
    
    :param size: 50sin(45), 50sin2(45), 50sin2(45) pixel side length triangle
    :pre: corner clockwise of the base, 
        facing the opposite base angle, pen down
    :post: from the origin angle/postion rotate counterclockwise 90 degrees,
        move one base length away, then face the original angle, pen down
    """
    t.begin_fill()
    t.forward(BLUEEDGE)
    t.left(135)
    t.forward(TSIDE)
    t.left(90)
    t.forward(TSIDE)
    t.end_fill()
    t.right(135)
    t.penup()
    t.forward(BLUEEDGE)
    t.left(180)
    t.pendown()

def drawthreetriangles(BLUEEDGE, TSIDE):
    """
    Draws three triangles and fills their colors in by calling upon 
    drawtriangle(). This function is called upon by drawpattern2(). First 
    draws the red triangle, then an adjacent cyan triangle, then an adjacent 
    red triangle, and ends in a position to draw the next set of triangles.
    
    :param triangle size: 50sin(45), 50sin2(45), 50sin2(45) pixel side lengths
    :param fillcolor: red, cyan
    :pre: any corner of the square drawn in dbsquare(), turtle facing away from 
        the square, perpendicular to the edge of the square, in the most 
        counter clockwise orientation, pen down
    :post: counterclockwise adjacent corner of origin corner, turtle facing 
        away from the square/ perpendicular to the edge of the square/ 
        in the most counter clockwise orientation, pen down
    """
    t.fillcolor("red")
    drawtriangle(BLUEEDGE, TSIDE)
    t.fillcolor("cyan")
    drawtriangle(BLUEEDGE, TSIDE)
    t.fillcolor("red")
    drawtriangle(BLUEEDGE, TSIDE)
    t.penup()
    t.forward(BLUEEDGE)
    t.left(90)
    t.forward(BLUEEDGE)
    t.right(90)
    t.pendown()

def drawpattern2(BLUEEDGE, TSIDE):
    """
    Draws pattern 2. First it calls upon dbsquare() to draw the dark blue 
    square in the center of the block. Then, it calls the threetriangles() 
    function to draw a red, cyan, and red equal isoceles right angle 
    triangles. threetriangles() is executed 4 times for each edge of the dark 
    blue square. Then the turtle returns to the pre position/orientation.

    :param square size: 50sin(45) * 50sin(45) pixels
    :param square fillcolor: dark blue
    :param triangle size: 50sin(45), 50sin2(45), 50sin2(45) pixel side lengths
    :param triangle: fillcolor: red, cyan
    :param triangle count: 12
    :pre: Center of the middle block, turtle facing north, pen up
    :post: -"-
    """
    t.pendown()
    #draws the dark blue square
    drawDBsquare(BLUEEDGE)
    #draws the three triangles along each face of the square
    for i in range(4):
        drawthreetriangles(BLUEEDGE, TSIDE)
    #return to pre postion/orientation
    t.penup()
    t.left(180)
    t.forward(BLUEEDGE * 0.5)
    t.right(90)
    t.forward(BLUEEDGE * 0.5)
    t.left(180)

def drawray(RAYSIDE, RAYBASE):
    """
    Draws a narrow isoceles triangle to be called upon by drawthesun().

    :param size: 37.5cos(10), 37.5cos(10), 75sin(10) pixel side length 
        triangle
    :pre: counterclockwise corner from the base, turtle facing vertex angle, 
        pen up or down
    :post: clockwise corner from the base, turtle facing same direction as
        start of the function, pen down
    """
    t.pendown()
    t.begin_fill()
    t.forward(RAYSIDE)
    t.left(160)
    t.forward(RAYSIDE)
    t.left(100)
    t.forward(RAYBASE)
    t.end_fill()
    t.left(180)
    t.forward(RAYBASE)
    t.right(60)

def drawsurface(RAYBASE):
    """
    Draws an equilateral triangle to be called by drawthesun().

    :param size: 75sin(10) pixel side length equilateral triangle
    :pre: Any corner of the triangle, turtle facing an opposite corner, pen 
        up or down
    :post: counter clockwise adjacent corner from original corner, turtle
        facing 20 degrees counter clockwise from original direction pen down
    """
    t.begin_fill()
    for i in range(3):
        t.forward(RAYBASE)
        t.left(120)
    t.end_fill()
    t.penup()
    t.forward(RAYBASE)
    t.pendown()
    t.left(20)
    
def drawsuncenter(RAYBASE):
    """
    Fills and colors the remaining center of The Sun yellow. To be called by
    drawthesun().

    :param fillcolor: yellow
    :pre: same as post of surface()
    :post: same position as pre, turtle facing 
    """
    t.left(100)
    t.forward(RAYBASE)
    t.begin_fill()
    t.fillcolor("yellow")
    for i in range(18):
        t.left(120)
        t.forward(RAYBASE)
        t.right(140)
        t.forward(RAYBASE)
    t.end_fill()

def drawthesun(RADIUS, RAYBASE, RAYSIDE):
    """
    Draws and colors The Sun pattern. First it draws each ray using drawray() 
    in a circular path with alternating gold yellow fills with the bases of 
    the triangle facing inward. Then it draws the suns surface as equilateral 
    triangles using the drawsurface() function that share an edge with the 
    base of each ray triangle. The equilateral triangles share the color 
    scheme of orange and gold using with the non matching color of their 
    adjacent ray. Then the center pattern is drawn and filled yellow using 
    drawsuncenter() and the turtle returns to its pre postion/orientation.
    
    :param ray size: 37.5cos(10), 37.5cos(10), 75sin(10) pixel side length 
        triangle
    :param ray triangle count: 18
    :param surface size: 75sin(10) pixel side length equilateral triangle
    :param surface triangle count: 18
    :param ray fillcolor: gold, orange
    :param surface fillcolor: orange, gold
    :param suncenter fillcolor: yellow
    :pre: Center of the right most block, turtle facing north, pen up
    :post: -"-
    """
    t.forward(RADIUS)
    t.right(90)
    t.forward(RAYBASE * 0.5)
    t.left(100)
    #Draws a symetricall loop of rays in alternating gold, orange colors
    for i in range(9):   
        t.fillcolor("gold")
        drawray(RAYSIDE, RAYBASE)
        t.fillcolor("orange")
        drawray(RAYSIDE, RAYBASE)
    t.left(80)
    #Draws surface triangles at each ray base alternating orange/gold colors
    for i in range(9):
        t.fillcolor("orange")
        drawsurface(RAYBASE)
        t.fillcolor("gold")
        drawsurface(RAYBASE)
    #fills in center of the sun
    drawsuncenter(RAYBASE)
    #return to pre postion/orientation
    t.penup()
    t.left(180)
    t.forward(RAYBASE)
    t.left(80)
    t.forward(0.5 * RAYBASE)
    t.left(90)
    t.forward(RADIUS)
    t.right(180)

def main():
    """
    This function opens a turtle window with a sped up turtle. Then it 
    draws three quilt desings as three blocks with distinct patterns 
    conatined inside them. Blocks are neatly organized equadistantly in a 
    row. The function draws the surrounding block first then the pattern 
    within; beginning with the leftmost block drawn first and ending with 
    the rightmost. It finishes with the turtle in a position to begin drawing 
    a 4th block.

    :param block size: 200 * 200 pixels
    :pre: origin turtle facing east, pen up or down
    :post: 340 pixels right of origin and 100 pixels below origin, 
        turtle facing east, pen up
    """
    TurtleGoFast()
    StartingPos(SIZE)
    drawblock(SIZE, INNER)
    MoveCenter(SIZE)
    drawpattern1(MHYPOT, MSIDE, PHYPOT, PSIDE)
    MoveNext(SIZE)
    drawblock(SIZE, INNER)
    MoveCenter(SIZE)
    drawpattern2(BLUEEDGE, TSIDE)
    MoveNext(SIZE)
    drawblock(SIZE, INNER)
    MoveCenter(SIZE)
    drawthesun(RADIUS, RAYBASE, RAYSIDE)
    MoveNext(SIZE)
    t.mainloop()

if __name__ == '__main__':
    main()

