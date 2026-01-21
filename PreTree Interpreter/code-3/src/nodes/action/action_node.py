"""
CSCI-603 PreTee Lab
Author: RIT CS

This class is an abstraction for all nodes that performs an action but doesn't compute a result.
All action nodes have one math expression as a child. By definition, these nodes
may change the state of the program, i.e. the symbol table.
"""

from abc import ABC, abstractmethod

from nodes.expression.math_node import MathNode
from nodes.pretee_node import PreTreeNode

class ActionNode(PreTreeNode, ABC):
    __slots__ = "_expression"
    _expression: MathNode

    def __init__(self, expression: MathNode):
        """
        Create a new action node
        :param: the child math expression
        """
        self._expression = expression

    @abstractmethod
    def execute(self) -> None:
        """
        Performs the action represented by this node.
        :return: None
        """
        pass

    def get_expression(self):
        """
        Returns the child math expression.
        This method is used by the subclasses that need access to the
        math expression.
        :return: the child expression
        """
        return self._expression
