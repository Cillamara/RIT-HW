"""
CSCI-603 Lab 6: Rescue Mission
Author: Liam Cui
"""
from Player import *
from Abstract_Data_Types import Queue

class Bunker(builtins.object):
    """
    The Bunker class that holds the soldiers.
    """
    __slots__ = "_num_soldiers", "_soldiers"
    _num_soldiers: int
    _soldiers: Queue

    def __init__(self, num_soldiers: int):
        """
        Create the bunker and populate it with the given number of soldiers.
        Each soldier will be created with an id ranging between 1 and
        num_soldiers.

        :param num_soldiers: the total number of soldiers that start in the
            bunker.
        """
        self._num_soldiers = 0
        self._soldiers = Queue()
        for i in range(num_soldiers):
            self.fortify_soldier(Player(i + 1, Role.SOLDIER))

    def deploy_next_soldier(self):
        """
        Remove the next soldier from the front of the bunker to be deployed
        on a rescue attempt.

        :pre: The bunker is not empty.
        :return: the soldier at the front of the bunker.
        """
        self._num_soldiers -= 1
        return self._soldiers.dequeue()

    def fortify_soldier(self, soldier: Player) -> None:
        """
        Add the soldier at the end of the bunker.

        :param soldier: The soldier to add.
        :return: None
        """
        self._soldiers.enqueue(soldier)
        self._num_soldiers += 1

    def get_num_soldiers(self) -> int:
        """
        Get how many soldiers are in the bunker.

        :return: The number of soldiers in the bunker.
        """
        return self._num_soldiers

    def has_soldiers(self) -> bool:
        """
        Are soldiers left in the bunker?

        :return: whether the bunker has soldiers or not.
        """
        if self.get_num_soldiers() == 0:
            return False
        else:
            return True