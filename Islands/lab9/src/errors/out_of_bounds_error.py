"""
CSCI-603 Islands Lab
Author: Liam Cui

A custom exception class for when a player claims a cell that is not within the
confines of the gameboard.
"""
class OutOfBoundsError(Exception):
    pass