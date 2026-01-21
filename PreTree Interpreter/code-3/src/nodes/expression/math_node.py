"""
CSCI-603 PreTee Lab
Author: RIT CS

An abstraction of all expression nodes that can be evaluated to
get an integer value as result. By definition, these nodes do not
alter the state of the program, i.e. the symbol table.
"""

from abc import ABC, abstractmethod

from nodes.pretee_node import PreTreeNode


class MathNode(PreTreeNode, ABC):

    @abstractmethod
    def evaluate(self) -> int:
        """
        Returns the mathematical evaluation of this node.
        :return: the result of this operation (an integer value)
        """
        return 0
