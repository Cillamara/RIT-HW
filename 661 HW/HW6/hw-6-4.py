"""
author: Liam Cui
CSCI 661: HW6 Q4
"""

import turtle
from time import sleep


######################################################################################################
#
# Function draw_square draws a square using turtlegraphics with the following parameters:
#
# – t, a turtlegraphics object variable.
# – x pos, an integer referencing the x-coordinate of the top-left corner of the square to be drawn
# – y pos, an integer referencing the y-coordinate of the top-left corner of the square to be drawn
# – side len, an integer referencing the length of the sides of the square to be drawn, in pixels (steps)
# – filled, a boolean value which is true only if the square is to be filled in (i.e. colored black)
#
######################################################################################################

def draw_square(t, x_pos, y_pos, side_len, filled):
  t.penup()
  t.goto((x_pos, y_pos))
  t.pendown()
  if filled:
    t.begin_fill()
  for i in range(4):
    t.forward(side_len)
    t.right(90)
  if filled:
    t.end_fill()


def add_object_1(Grid, x, y):
  Grid[x + 2][y + 2] = True
  Grid[x + 2][y + 1] = True
  Grid[x + 2][y] = True
  Grid[x + 1][y] = True
  Grid[x][y + 1] = True
  return Grid



def add_object_2(Grid, x, y, dim):
  for i in range(dim):
    for j in range(dim):
      Grid[x + i][y + j] = True
  return Grid



def add_object_3(Grid, x, y):
  Grid[x + 1][y] = True
  Grid[x][y + 1] = True
  Grid[x + 1][y + 1] = True
  Grid[x + 1][y + 2] = True
  Grid[x + 2][y + 2] = True
  return Grid



######################################################################################################
#
# Function draw_grid will draw a (dim x dim) grid of squares with the following input parameters:
#
# – t, a turtlegraphics variable.
# – x home, an integer referencing the x-coordinate of the top-left corner of the bottom-left square to be drawn.
# – y home, an integer referencing the y-coordinate of the top-left corner of the bottom-left square to be drawn.
# -   NOTE the vertical inversion - we draw bottom up
# – side len, an integer referencing the length of the sides of all (individual, small) squares.
# – gap len, an integer referencing the length of the gap between (individual, small) squares.
# – grid dim, an integer representing the length of the columns and rows
#     (the number of squares to be drawn in each column and row).
# – Grid, a 2-D matrix of boolean values.
# -   NOTE that Grid can also be conceived as a list of lists, with each sublist corresponding to a grid column
# -   NOTE that each square (i,j) in the matrix should be colored (black) only if Grid[i][j] == True
#
# - NOTE that draw_square is provided above and can be called as a subroutine
#
# Output: screen rendering of Grid with entries filled only if corresponding boolean entry is true
#
######################################################################################################

def draw_grid(t, x_home, y_home, side_len, gap_len, dim, Grid):

  turtle.tracer(0) #CAUTION Do not erase this line - it must be called first in this function

  #NOTE Your code here
  for x in range(dim):
    for y in range(dim):
      if x == 0:
        x_gap = gap_len
      else:
        x_gap = 0
      if y == 0:
        y_gap = gap_len
      else:
        y_gap = 0
      current_x = x_home + (x * (x_gap + side_len))
      current_y = y_home + (y * (y_gap + side_len))
      if Grid[x][y]:
        filled = True
      else:
        filled = False
      draw_square(t, current_x, current_y, side_len, filled)
  turtle.update() #CAUTION Do not erase this line - it must be called last in this function



######################################################################################################
#
# Function generate_blank_grid will populate a (dim x dim) boolean 2-D matrix with all False entries
#
# Input: dim, the (unique) length of each dimension in the (square) 2-D output matrix
#
# Output: a (dim x dim) 2-D matrix of boolean values all set to False
#
######################################################################################################


def generate_blank_grid(dim):
  return [[False for x in range(dim)] for y in range(dim)]


######################################################################################################
#
# Function modify_grid will modify a (dim x dim) grid of squares as per the handout
#
# Input: Grid_In, a 2-D matrix of boolean values.
#
# Output: Grig_Out (a 2-D matrix of boolean values)
#   the modified input grid which has been updated as per the specificaion in the HW handout
#
######################################################################################################

def modify_grid(Grid_in):
  dim = len(Grid_in)
  Grid_out = [row.copy() for row in Grid_in]
  for x in range(dim):
    for y in range(dim):
      truth_counter = 0
      for i in (-1, 0, 1):
        for j in (-1, 0, 1):
          if i == 0 and j == 0:
            continue
          nx, ny = x + i, y + j
          if 0 <= nx < dim and 0 <= ny < dim:
            truth_counter += Grid_in[nx][ny]
      if Grid_in[x][y]:
        Grid_out[x][y] = truth_counter in (2, 3)
      else:
        Grid_out[x][y] = truth_counter == 3
  return Grid_out









######### Main Program

t = turtle.Turtle(visible=False)
turtle.speed('fastest')
side_len = 20
gap_len = 2
x_home = y_home = -300
dim = 32

Grid = generate_blank_grid(dim)

#CAUTION only uncomment one of the following three function calls at a time, and make sure not to modify generate_blank_grid() call just above
Grid = add_object_1(Grid, 1, 27)
#Grid = add_object_2(Grid, 11, 12, 9)
#Grid = add_object_3(Grid, 14, 15)


for i in range(512):
  t.clear()
  turtle.hideturtle()
  draw_grid(t, x_home, y_home, side_len, gap_len, dim, Grid)
  Grid = modify_grid(Grid)
  if  (i == 0): sleep(5)


turtle.clear()
turtle.done()

