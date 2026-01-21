"""
CSCI-603 Lab 4: Skyscrapers
Author: Liam Cui

A program which reads a file containing the dimensions, clues, and solutions.
This program then draws the puzzle according to the file and checks whether the
solution is valid or not. It then displays a message showing the solution to be
valid or the errors in the solution.
"""
from dataclasses import dataclass

def get_file():
    """
    Retrieves the file from the working directory and prints that the program
        is working on that file.

    :return: the file path
    """

    filename = input('Enter a file path: ')
    print('Validating solution in file: ' + filename)
    return filename

@dataclass
class Puzzle:
    """
    A data class representing a skyscrapers puzzle. It can read from a
    file and stores the puzzle grid side length size, puzzle clues as a 2D
    list, and file solution as a 2D list. It also allows the user to retrieve
    the entire row or column of the file's solution through getter methods.
    """

    #Fields
    rules: list[list[int]] #rules the buildings must follow as a 2D list
    buildings: list[list[int]] #buildings heights as a 2D list

    #Methods
    def process_file(filename: str):
        """
        A part of the Puzzle data structure. This function is called in main.
        This function opens a file out of the working directory. It reads and
        stores the first line of the file as the dimensions of the puzzle. It
        then reads and appends the consecutive 4 lines as a 2D list of integers
        which represents the clues of the puzzle. The remaining lines are the
        rows of the buildings' heights which are stored in a 2D list. If there
        are any errors in how the file is written, error messages are printed
        and the program terminates.

        :param filename: the file path
        :return buildings: the 2D list of building heights
        :return rules: the clues indicating the number of buildings visible
        :return dimension: size length of the square puzzle
        """
        rules = list()
        buildings = list()
        with open(filename) as f:
            dimension = int(f.readline())
            line_count = 1
            for line in f:
                line_count += 1
                numbers = line.strip("\n").split(" ")
                if len(numbers) != dimension and line_count < 5 + dimension:
                    #if there are too few or too many lines in the file
                    print('Found the following problems:\n  invalid line ',
                          line_count)
                    quit(0)
                if len(rules) < 4: #keep appending rules when less than 4
                    numbers = list(map(int, numbers)) #list(str) >>> list(int)
                    rules.append(numbers)
                elif len(buildings) < dimension: #keep appending solution rows
                    numbers = list(map(int, numbers)) #list(str) >>> list(int)
                    buildings.append(numbers)
        if len(rules) < 4 or len(buildings) != dimension:
            print('Found the following problems:\n  Invalid number of lines')
            quit(0)
        return buildings, rules, dimension

    def get_rows(self, rows: int):
        """
        A getter for rows of buildings

        :param rows: the row number
        :return: the heights of each building given the row
        """
        return self.buildings[rows]

    def get_cols(self, cols: int):
        """
        A getter for columns of buildings

        :param cols: the column number
        :return: the heights of each building given the column
        """

        full_col = list()
        for row in self.buildings:
            col_value = row[cols]
            full_col.append(col_value)
        return full_col

def draw_puzzle(dimension, puzzle: Puzzle):
    """
    Draws the puzzle on the screen

    :param puzzle: the data structure representing the puzzle
    :print: an image of the puzzle
    """
    rules = puzzle.rules

    print('  ' + ' '.join(map(str, rules[0])))
    print('  ' + ('--' * dimension) + '  ')
    for i in range(dimension):
        print(str(rules[3][i]) + '|' + ' '.join(map(str, puzzle.get_rows(i)))
              + '|' + str(rules[1][i]))
    print('  ' + ('--' * dimension) + '  ')
    print('  ' + ' '.join(map(str, rules[2])) + '\n')

def check_heights(buildings, dimension):
    """
    Searches the entire buildings 2D list and checks whether each building
    height is within the possible bounds given that no building is less than
    one or taller than the dimension. If any are found to be out of bounds, the
    function stores it as an error message called problem and return that and a
    False boolean indicating that the puzzle is invalid.

    :param buildings: The 2D list of building heights
    :param dimension: The side dimension of the square puzzle
    :return valid: A boolean indicating if the puzzle follows all the rules
    :return problems: A string containing all the error messages
    """

    valid = True
    problems = "" # empty string for error messages
    for i in range(dimension):
        for j in range(dimension):
            if  buildings[i][j] < 1 or buildings[i][j] > dimension:
                problems = (problems + 'invalid value ' + str(buildings[i][j])
                            + ' at position (' + str(i) + ', ' + str(j) + ')')
                valid = False
    return valid, problems


def check_rule(heights: list, rule: int, dimension: int):
    """
    This function checks whether the list of heights follows the given clue.
    It determines how many buildings are visible given the list of heights by
    checking whether there is a next tallest building or not when iterating
    through the list. This number of times there has been a new tallest
    building is tallied from 1 and then compared to the rule to see if it
    matches. A boolean indicating whether it matches the rule is returned. This
    function is called by check_clues.

    :param heights: the list of building heights
    :param rule: the integer value of number of buildings visible
    :param dimension: size length of the square puzzle
    :return: Boolean indicating if the rule is correctly followed or not
    """

    buildings_seen = 0
    height = 0
    for i in range(dimension):
        if height < heights[i]:
            height = heights[i]
            buildings_seen += 1
    if buildings_seen == rule:
        return True
    else:
        return False


def check_clues(buildings, rules, dimension, valid, problems):
    """
    This function checks whether there are duplicate building heights in every
    row and column. It matches rules to each row and column of heights in the
    specified direction and uses the check_rule to determine whether the clues
    have been followed correctly or not. If there are any errors found within
    the puzzle, the function stores error messages in a string called
    'problems' which carries over from the check_heights function. The validity
    check from the check_heights function is also carried over.

    :param buildings: the 2D list of building heights
    :param rules: the clues indicating the number of buildings visible
    :param dimension: the size of the square puzzle
    :param valid: Boolean of whether the configuration is valid or not
    :param problems: String of all the errors in the configuration
    :return valid: a boolean indicating if the puzzle follows all the rules
    """

    puzzle = Puzzle(rules = rules, buildings = buildings)
    for i in range(4):
        for j in range(dimension):
            if i == 0:  # Clues at north looking south values, left to right
                row_col = puzzle.get_cols(j)
                description = '    Top clue at position '
                if len(set(row_col)) != dimension:
                    problems = problems + ('    Duplicate height exists at '
                                           'column ') + str(j) + '.\n'
                    valid = False
            elif i == 1:  # Clues at east looking west values, top to bottom
                row_col = puzzle.get_rows(j)[::-1]
                description = '    Right clue at position '
                if len(set(row_col)) != dimension:
                    problems = (problems + '    Duplicate height exists '
                                           'at row ' + str(j) + '.\n')
                    valid = False
            elif i == 2:  # Clues at south looking north values, left to right
                row_col = puzzle.get_cols(j)[::-1]
                description = '    Bottom clue at position '
            elif i == 3:  # Clues at west looking east values, top to bottom
                row_col = puzzle.get_rows(j)
                description = '    Left clue at position '
            check = check_rule(row_col, rules[i][j], dimension)

            if not check:
                problems = problems + description + str(j) + ' violated.\n'
                valid = False
                break
    return valid, problems

def print_valid(valid: bool, problems: str):
    """
    Prints a message declaring whether the puzzle is valid or if any errors
    have been found

    :param valid: a boolean indicating if the puzzle follows all the rules
    :print: if valid, prints a message saying the puzzle is valid. If invalid,
        prints a message stating errors found.
    """

    if valid:
        print('The puzzle is valid!')
    else:
        print('Found the following problems:\n' + problems)

def main():
    """
    The main function.
    """

    filename = get_file()
    buildings, rules, dimension = Puzzle.process_file(filename)
    draw_puzzle(dimension, puzzle = Puzzle(rules = rules, buildings =
    buildings))
    valid, problems = check_heights(buildings, dimension)
    valid, problems = check_clues(buildings, rules, dimension, valid, problems)

    print_valid(valid, problems)

if __name__ == '__main__':
    main()


