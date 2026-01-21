"""
CSCI-603 Islands Lab
Author: Liam Cui

An enum representing who owns a cell. RED and BLUE represent the players while
NONE represents no ownership.
"""
import enum
class Role(enum.Enum):
    NONE = 'NONE'
    RED = 'RED'
    BLUE = 'BLUE'
    def __str__(self):
        return self.value