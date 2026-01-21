"""
CSCI-603 Islands Lab
Author: Liam Cui

The class that represents the board the island game is played. This class is
initialized by an Island instance.
"""
from cell import Cell
from errors.already_claimed_error import AlreadyClaimedError
from errors.out_of_bounds_error import OutOfBoundsError
from player import Role


class GameBoard:
    """
    This class represents a grid based board where the island game is played
    on. The board initializes each square as a cell. These cells are then
    stored as values of a dictionary which can be accessed by their row and
    column pair as the key. These cells can be claimed by any player if not
    already claimed.
    """
    __slots__ = ("_cells", "_red_claimed", "_blue_claimed", "_dim_row",
                 "_dim_col", "_game_over")
    # Dictionary for the cells in the board. Keys stored as int pairs
    _cells: dict[int, int: Cell]
    # set of cells claimed by player RED
    _red_claimed: set[Cell]
    # set of cells claimed by player BLUE
    _blue_claimed: set[Cell]
    # The number of rows in the board
    _dim_row: int
    # The number of columns in the board
    _dim_col: int
    # Is the game over
    _game_over: bool

    def __init__(self, num_row: int, num_col: int):
        """
        Initialize the game board
        :param num_row: The number of rows in the game board
        :param num_col: The number of columns in the game board
        """
        self._dim_row = num_row
        self._dim_col = num_col
        self._cells = {}
        # Initialize every cell on the board
        for i in range(num_row):
            for j in range(num_col):
                self._cells[i, j] = Cell(i, j, self)
        # Populate each cell's neighbor dictionary
        for cell in self._cells.values():
            cell.add_neighbors()
        self._red_claimed = set()
        self._blue_claimed = set()
        self._game_over = False

    def get_cell(self, row: int, col: int) -> Cell:
        """
        Returns the cell with the particular row and column values.
        :param row: the row number
        :param col: the column number
        :return: the cell at the specified row and column
        """
        return self._cells[row, col]

    def claim(self, row: int, col: int, role: Role) -> None:
        """
        Takes a row value, column value, and a role, and attempts to claim the
        cell at that row and column value under the ownership of the given
        role. If the row or column is outside the board, raises an
        out-of-bounds error. If the cell is already claimed, raises an
        already-claimed error. After a successful claim, check_game_over() is
            called to determine if the game is over.
        :param row: The row number
        :param col: The column number
        :param role: The Role which will claim the cell
        """
        if row < 0 or row >= self._dim_row or col < 0 or col >= self._dim_col:
            raise OutOfBoundsError
        elif self.get_cell(row, col).get_role() != Role.NONE:
            raise AlreadyClaimedError
        else:
            cell = self.get_cell(row, col)
            cell.claim(role)
            self.get_claimed(role).add(cell)
            island = cell.get_island()
            self.check_game_over(role, island)

    def check_game_over(self, role: Role, island: set[Cell]) -> None:
        """
        Called every time a cell is claimed by a player. Given the player's
        role and the set of cells of the island of the recently claimed cell,
        the function checks if that island touches top to bottom of the board
        if the player is RED, of if that island touches left to right if they
        are BLUE, or if all cells have been claimed.
        If any of those conditions are fulfilled, then the _game_over boolean
        is set to True.

        :param role: The Role of the player
        :param island: the set of cells that belong to the island that the
            recently claimed cell belongs to.
        """
        # The set of unique rows(if role is RED) or columns(if role is BLUE)
        dim_set = set()
        for cell in island:
            if role == Role.RED:
                dim_set.add(cell.get_row())
            elif role == Role.BLUE:
                dim_set.add(cell.get_col())
        if role == Role.RED:
            if len(dim_set) == self._dim_row: # Island touches top/bottom ends
                self._game_over = True
        elif role == Role.BLUE:
            if len(dim_set) == self._dim_col: # Island touches left/right ends
                self._game_over = True
        # If all cells are claimed
        elif len(self._red_claimed) + len(self._blue_claimed) == len(
                self._cells):
            self._game_over = True

    def get_game_over(self) -> bool:
        """
        Is the game over?
        :return: whether the game is over
        """
        return self._game_over

    def print_board(self) -> None:
        """
        Prints the game board. Cells are printed as a '*' if owned by RED and
        '-' if owned by BLUE.
        """
        board_str = "\n    "
        for i in range(self._dim_col):
            board_str += (" " + str(i) + " ")
        board_str += "\n    "
        for i in range(self._dim_col):
            board_str += "___"
        board_str += "\n"
        for i in range (self._dim_row):
            board_str += (str(i) + " | ")
            for j in range (self._dim_col):
                board_str += (" " + str(self.get_cell(i, j)) + " ")
            board_str += "\n"
        print(board_str)

    def get_score(self, player: Role) -> int:
        """
        A function that returns the number of islands claimed by a specified
        player.
        :param player: The role of the player
        :return: the number of islands claimed by the specified player
        """
        claimed_cells = self.get_claimed(player).copy()
        return self.__get_score_rec(claimed_cells, 0)

    def __get_score_rec(self, claimed_cells: set[Cell], island_count: int = 0)\
            -> int:
        """
        Recursive helper function for get_score. Takes a set of claimed cells
        and removes a single cell. Then gets all the cell members of the island
        that the cell belonged to and removes them from the set of claimed
        cells. This increments a counter for the number of islands by one. This
            is recursively called until the set of claimed cells is empty.

        :param claimed_cells: a set of claimed cells
        :param island_count: the number of islands already counted
        :return: the number of islands that make up the set of claimed cells.
        """
        if len(claimed_cells) > 0:
            island = claimed_cells.pop().get_island()
            island_count += 1
            claimed_cells -= island
            return self.__get_score_rec(claimed_cells, island_count)
        return island_count

    def get_dim_col(self) -> int:
        """
        Get the number of columns in the game board
        :return: The number of columns in the game board
        """
        return self._dim_col

    def get_dim_row(self) -> int:
        """
        Getter for the number of rows in the game board
        :return: the number of rows in the game board
        """
        return self._dim_row

    def show_islands(self, player: Role) -> None:
        """
        Given a player. Prints all the islands claimed by the player. Each
        island is represented by and printed as that island's size, and the
            cells that comprise it.
        :param player: The role of the player whose claimed the islands will be
            printed.
        """
        claimed_cells = self.get_claimed(player)
        remaining_cells = claimed_cells.copy()
        island_count = 0
        islands_str = ""
        for cell in claimed_cells:
            if cell in remaining_cells:
                island = cell.get_island()
                island_count += 1
                remaining_cells -= island
                islands_str += f"Island[size:{len(island)}]\n"
                islands_str += ("   " + repr(island).replace(
                    "{", "").replace("}", "").replace(
                    ", ", "") + "\n")
        print(f"Player: {player}, islands: {island_count}")
        print(islands_str, end = "")


    def get_claimed(self, player: Role) -> set[Cell]:
        """
        Returns the set of claimed cells associated with the specified player.
        This method exists in order to avoid repetition.
        :param player: Role.RED or Role.BLUE
        :return: _red_claimed or _blue_claimed depending on role parameter
        """
        if player == Role.RED:
            return self._red_claimed
        elif player == Role.BLUE:
            return self._blue_claimed
        else:
            raise ValueError



