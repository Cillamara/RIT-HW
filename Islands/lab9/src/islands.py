"""
CSCI-603 Islands Lab
Author: Liam Cui

A command-line interface program to play the pen-and-paper game, Islands. All
user interaction is done via the standard input and output. The program Takes
arguments from the command line to determine the size of the board the game
will be played on and then the players can play the game.
"""
from sys import argv

from errors.already_claimed_error import AlreadyClaimedError
from errors.out_of_bounds_error import OutOfBoundsError
from player import Role
from gameboard import GameBoard

class Islands:
    """
    The pen-and-paper game, Islands. The game board and its cells are created
    upon initialization and the game begins by running the play() method. Once
    the game is completed, the program continues to accept non-claim
    commands until a quit command is entered. The program terminates when a
    quit command is entered.
    """
    __slots__ = "_gameboard", "_turn",
    # The board the game will be played on.
    _gameboard: GameBoard
    # The turn counter. Turns will only increment after claiming an island.
    _turn: int

    def __init__(self, num_rows: int, num_col: int):
        """
        initializes the island game
        :param num_rows: The number of rows in the island game
        :param num_col: The number of columns in the island game
        """
        self._gameboard = GameBoard(num_rows, num_col)
        self._turn = 0

    def play(self) -> None:
        """
        Play the island game! Provides a list of possible commands and then
        prompts the players to play the game alternating turns taken. The
        players can continue to play until someone decides
        to quit. Once the game over condition is fulfilled the winner is
        determined. Players can no longer claim cells from this point on,
        however the show, help, and quit commands are still available.

        below are the commands that a player can input:

        c(laim) row column -- claim the cell in that row and column
        s(how) {red|blue}  -- show the islands by the given player
        h(elp)             -- display this list of commands
        q(uit)             -- quit this game
        """
        help_str = ("List of commands available:\n"
                "   c(laim) row column -- claim the cell in that row and "
                    "column\n"
                "   s(how) {red|blue}  -- show the islands by the given "
                    "player\n"
                "   h(elp)             -- display this list of commands\n"
                "   q(uit)             -- quit this game\n")
        print("Welcome to Islands!\n" + help_str)
        self._gameboard.print_board()
        self.print_score_board()
        while True:
            if self._turn % 2 == 0:
                player = Role.RED
            else:
                player = Role.BLUE
            command = input("> ")
            command = command.split()
            try:
                match command[0].lower():
                    case 'c' | "claim":
                        if self._gameboard.get_game_over():
                            continue
                        else:
                            print(f"{str(player)}'s move...")
                            self._gameboard.claim(int(command[1]),
                                              int(command[2]), player)
                            self._turn += 1
                    case 'h' | "help":
                        print(help_str)
                        continue
                    case 's' | "show":
                        if len(command) != 2:
                            raise ValueError
                        else:
                            if command[1].upper() == "RED":
                                self._gameboard.show_islands(Role.RED)
                            elif command[1].upper() == "BLUE":
                                self._gameboard.show_islands(Role.BLUE)
                            else:
                                raise ValueError
                            continue
                    case 'q' | "quit":
                        break
                    case _:
                        raise ValueError
            except (ValueError, IndexError):
                print("Invalid user input!")
                continue
            except (OutOfBoundsError, AlreadyClaimedError):
                print("Invalid cell coordinates!")
                continue
            self._gameboard.print_board()
            self.print_score_board()
        print("Bye!")

    def print_score_board(self) -> None:
        """
        prints the score board
        If the game is over prints who won. If the game is still going on,
        prints the move number and whose turn it is.
        """
        if self._turn % 2 == 0:
            player = Role.RED
        else:
            player = Role.BLUE
        red_score = self._gameboard.get_score(Role.RED)
        blue_score = self._gameboard.get_score(Role.BLUE)
        print(f"Player: RED, islands: {red_score}, "
              f"Player: BLUE, islands: {blue_score}")
        if self._gameboard.get_game_over():
            if red_score > blue_score:
                print(f"\nRED wins {red_score} to {blue_score}!")
            elif blue_score > red_score:
                print(f"\nBLUE wins {blue_score} to {red_score}!")
            else:
                print(f"\nTie game {red_score} to {blue_score}!")
        else:
            print(f"Moves: {self._turn}, Turn: {player}")

def main():
    """
    The main function. Takes arguments from the command line to determine the
    size of the board the game will be played on and then plays that game.
    """
    try:
        if len(argv) == 3:
            num_rows = int(argv[1])
            num_cols = int(argv[2])
            if num_rows < 1 or num_cols < 1:
                raise ValueError
            game = Islands(num_rows, num_cols)
            game.play()
        else:
            raise ValueError
    except (ValueError, IndexError):
        print("Usage: python islands.py <num_rows> <num_cols>")

if __name__ == "__main__":
    main()