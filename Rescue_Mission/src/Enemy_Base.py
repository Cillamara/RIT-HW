"""
CSCI-603 Lab 6: Rescue Mission
Author: Liam Cui
"""
import builtins
from Abstract_Data_Types import Stack, Queue
import random
from Player import Player, Role


class Enemy_Base(builtins.object):
    """
    Enemy_Base(num_hostages: int, num_guerrillas: int, rnd: random.Random)

    This class represents the enemy base.  It contains a guard line of
    guerrillas that are guarding a group of hostages stored in a narrow cave
    with only an entrance and now way for the hostages to reorder themselves.
    """
    __slots__ = ("_hostages", "_guerrillas", "_rnd", "_num_hostages",
                 "_num_guerrillas")
    _hostages: Stack
    _guerrillas: Queue
    _num_hostages: int
    _num_guerrillas: int
    _rnd: random.Random

    def __init__(self, num_hostages: int, num_guerrillas: int,
                 rnd: random.Random):
        """
        Create the enemy base with a number of hostages, 1-num_hostages,
        pushed into the cave in order, and a number of guerrillas,
        1-num_guerrillas, added to the guard line in order.

        :param num_hostages: the number of hostages to start the enemy base
        :param num_guerrillas: the number of guerrillas to start the enemy base
        :param rnd: the seeded random number generator
        """
        self._hostages = Stack()
        self._guerrillas = Queue()
        self._num_hostages = 0
        self._num_guerrillas = 0
        self._rnd = rnd

        for i in range(num_hostages):
            self.add_hostage(Player(i + 1, Role.HOSTAGE))
        for i in range(num_guerrillas):
            self.add_guerrilla(Player(i + 1, Role.GUERRILLA))

    def add_guerrilla(self, guerrilla: Player) -> None:
        """
        Add a guerrilla at the end of the line

        :param guerrilla: the guerrilla to add
        :return: None
        """
        self._num_guerrillas += 1
        self._guerrillas.enqueue(guerrilla)

    def add_hostage(self, hostage: Player) -> None:
        """
        Add a hostage to the front of the cave

        :param hostage: the hostage to add
        :return: None
        """
        self._num_hostages += 1
        self._hostages.push(hostage)

    def get_guerrilla(self) -> Player:
        """
        Remove a guerrilla from the front of the line

        :pre: the guerrilla line is not empty
        :return: the guerrilla at the front of the line
        """
        if self.get_num_guerrillas() > 0:
            self._num_guerrillas -= 1
            return self._guerrillas.dequeue()

    def get_hostage(self) -> Player:
        """
        Get a hostage from the front of the cave

        :pre: the cave is not empty
        :return: the hostage at the front of the cave
        """
        self._num_hostages -= 1
        return self._hostages.pop()

    def get_num_guerrillas(self) -> int:
        """
        Get the number of guerrillas in line

        :return: the number of guerrillas
        """
        return self._num_guerrillas

    def get_num_hostages(self) -> int:
        """
        Get the number of hostages in the cave

        :return: the number of hostages
        """
        return self._num_hostages

    def rescue_hostage(self, soldier: Player) -> Player or None:
        """
        A soldier enters the enemy base and attempts to rescue a hostage.
        First they must defeat the guerrilla at the front of the guard line,
        then they can rescue one hostage at the front of the cave.  Follow
        these steps:

        1. Print the message "{soldier} enters enemy base...".

        2. Remove and hold onto the hostage at the front of the cave.

        3. If there are no guerrillas left in the base, return the hostage
            to the caller.

        4. Otherwise, get the next guerrilla from the guard line.

        5. Have the guerrilla roll the die, 1-100, and print the message,
            "{soldier} battles {guerrilla} who rolls a #".

        6. If the die roll is greater than the chance to defeat the soldier,
            the soldier declares victory over the guerrilla. The front hostage
            is then returned from the method.

        7. Otherwise, the guerrilla declares victory over the soldier. The
            hostage is pushed back onto the head of cave and the guerrilla is
            added to the end of the guard line.  No hostage is returned, e.g.
            None.

        :param soldier: the rescuing soldier
        :return: If a hostage was rescued. Otherwise, if the soldier has
            failed, returns None.
        """
        print(str(soldier) + " enters enemy base...")
        hostage = self.get_hostage()
        if self.get_num_guerrillas() > 0:
            guerrilla = self.get_guerrilla()
        else:
            # if there are no more guerillas, automatically rescue hostage
            return hostage
        roll = self._rnd.randint(1, 100)
        print(str(soldier) + " battles " + str(guerrilla)+ " who rolls a",
              roll)
        if roll > Player.GUERRILLA_CHANCE_TO_BEAT_SOLDIER:
            # soldier wins
            soldier.print_victory_message(guerrilla)
            return hostage
        else:
            # guerilla wins
            guerrilla.print_victory_message(soldier)
            self.add_hostage(hostage)
            self.add_guerrilla(guerrilla)

