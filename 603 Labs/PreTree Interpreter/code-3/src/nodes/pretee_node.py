"""
CSCI-603 PreTee Lab
Author: RIT CS

This class represents the top level of abstraction of all nodes in a parse tree.
"""
from abc import abstractmethod, ABC


class PreTreeNode(ABC):

    @abstractmethod
    def emit(self) -> str:
        """
        Returns the string representation of the node
        in infix form
        """
        return ""
