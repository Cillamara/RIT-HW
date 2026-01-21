"""
CSCI-603 Islands Lab
Author: Liam Cui

The Cell class which represents each individual grid component of the board.
This class is initialized by a GameBoard instance.
"""
from player import Role

class Cell:
    """
    Class that represents a single cell of on the board of the island game.
    Each cell has a unique row column value pair which represents where on the
    board the cell is located. Each cell can also be claimed by a player which
    assigns ownership of the cell to the player. Claiming of a cell cannot be
    overridden. Each cell's neighboring cells are also stored as a dictionary.
    """
    __slots__ = "_row", "_col", "_role", "_neighbors", "_board"
    # The row and column values of the cell
    _row: int
    _col: int
    # Who owns the cell
    _role: Role
    # All adjacent cells on the board
    _neighbors: dict[int, int: 'Cell']
    # The board the cell belongs to
    _board: any

    def __init__(self, row: int, col: int, gameboard):
        """
        Initialize a cell. Neighbors will not be assigned to the cell during
        initialization.
        :param row: The row value assigned to the new cell
        :param col: The column value assigned to the new cell
        :param gameboard: The gameboard the cell belongs on.
        """
        self._row = row
        self._col = col
        self._role = Role.NONE
        self._neighbors = {}
        self._board = gameboard

    def __str__(self) -> str:
        """
        The string representation of a cell as the gameboard prints:
        :return: The string representation of a cell as the gameboard prints:
        """
        match self._role:
            case Role.NONE:
                return " "
            case Role.RED:
                return "*"
            case Role.BLUE:
                return "-"
            case _:
                raise ValueError
    def __repr__(self) -> str:
        """
        The string representation of the cells row column value pair.
        :return: The string representation of the cells row column value pair.
        """
        return f"({self._row},{self._col})"

    def add_neighbors(self) -> None:
        """
        Populates the _neighbors fields with all cells adjacent to itself on
        the board.
        """
        for i in range(3):
            for j in range(3):
                n_row = self._row + i - 1
                n_col = self._col + j - 1
                if ((n_row != self._row or n_col != self._col) and n_row >= 0
                        and n_col >= 0 and n_row < self._board.get_dim_row()
                        and n_col < self._board.get_dim_col()):
                    self._neighbors[n_row, n_col] = self._board.get_cell(n_row,
                                                                         n_col)

    def same_role(self, cell: 'Cell') -> bool:
        """
        Whether the parameter cell has the same role as the function caller.
        :param cell: a cell
        :return: True if the parameter cell has the same role as the caller.
            Otherwise, False.
        """
        return self._role == cell.get_role()

    def is_neighbor(self, cell: 'Cell') -> bool:
        """
        Whether the given parameter cell is a neighbor of the function caller.
        :param cell: a cell
        :return: True if the parameter cell is a neighbor of the function
            owner. Otherwise, False.
        """
        if self._neighbors.get((cell._row, cell._col)) is None:
            return False
        else:
            return True

    def claim(self, role: Role) -> None:
        """
        Pass a role to assign that role to this cell.
        :param role: a Player's role
        """
        self._role = role

    def get_island(self)-> set['Cell']:
        """
        Returns a set the cells that make up the island which this cell
        (function caller) belongs to. Uses the recursive helper function
        __get_island_rec().

        :return: the set of cells that make up the island that this cell is a
            part of.
        """
        island = set()
        return self.__get_island_rec(island, self._role)

    def __get_island_rec(self, island_members: set['Cell'], role: Role) \
            -> set['Cell']:
        """
        A recursive helper function for get_island(). This function is given a
        set of cells and a role and then adds the cell calling it to the set.
        It then traverses the neighbors of the caller. The neighbors of the
        same role and were not already present in the set, recursively call the
        function with the same role and updated set as parameters. Those
        recursive returns are union operated to the set of cells. The function
        returns a set of cells that are members of the same island.
        :param island_members: a set of cells that are a part of the same
            island. This can be an empty set.
        :param role:
        :return: a set of cells that are a part of the same island, If the
        initial parameter set of cells is empty, then the return is guaranteed
            to be all the members of the island that this instance belongs to.
        """
        island = island_members
        island.add(self)
        for cell in self._neighbors.values():
            if cell.get_role() == role:
                if cell not in island:
                    island.union(cell.__get_island_rec(island, role))
        return island

    def get_row(self) -> int:
        """
        Getter for the row value.
        :return: The row value.
        """
        return self._row

    def get_col(self) -> int:
        """
        Getter for the column value.
        :return: The column value.
        """
        return self._col

    def get_role(self) -> Role:
        """
        Getter for which role owns the cell (NONE, RED, or BLUE).
        :return: the role owning the cell.
        """
        return self._role
