"""
CSCI-603 Islands Lab
Author: Liam Cui

A custom exception class for when a player claims a cell that is already
claimed.
"""
class AlreadyClaimedError(Exception):
    pass