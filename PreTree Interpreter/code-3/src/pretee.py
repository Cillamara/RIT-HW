"""
CSCI-603 PreTee Lab
Author: RIT CS
Author: {YOUR NAME HERE}

The main program and class for a prefix expression interpreter of the
PreTee language.

Usage: python3 pretee.py source-file.pre
"""

import sys  # argv
import errors.runtime_error  # runtime_error.RuntimeError


class PreTee:
    """
    The PreTee class consists of:
    - the name of the source file (str)
    - the symbol table (dict[key=str, valur=int])
    - the parse trees: a list of the root nodes for valid, non-commented
        line of code
    - the line number: when parsing, the current line number in the source
        file (int)
    - a syntax error flag: indicates whether a syntax error occurred during
        parsing (bool).  If there is a syntax error, all the parse trees will
        not be evaluated
    """
    __slots__ = ''

    # the tokens in the language for statements
    COMMENT_TOKEN = '#'
    ASSIGNMENT_TOKEN = '='
    PRINT_TOKEN = '@'

    def __init__(self, src_file):
        pass

    def parse(self) -> None:
        pass

    def emit(self) -> None:
        pass

    def evaluate(self):
        pass


def main():
    """
    The main function prompts for the source file, and then does:
        1. Compiles the prefix source code into parse trees
        2. Prints the source code as infix
        3. Executes the compiled code
    :return: None
    """
    if len(sys.argv) != 2:
        print('Usage: python3 pretee.py source-file.pre')
        return

    pretee = PreTee(sys.argv[1])
    print('PRETEE: Compiling', sys.argv[1] + '...')
    pretee.parse()
    print('\nPRETEE: Infix source...')
    pretee.emit()
    print('\nPRETEE: Executing...')
    try:
        pretee.evaluate()
    except errors.runtime_error.RuntimeError as e:
        # on first runtime error, the supplied program will halt execution
        print('*** Runtime error:', e)


if __name__ == '__main__':
    main()
