""" 
file: pycount.py
language: python3
author: CS RIT
description: Word Count Program.
            This version uses the built-in dict type.
"""


def word_count(filename):
    """
    Report on the frequency of different words in the
    file named by the argument.
    """
    d = {}
    inFile = open(filename)

    for line in inFile:
        for word in line.split():
            word = word.strip(",.\"\';:-!?").lower()
            if word not in d:
                d[word] = 1
            else:
                d[word] += 1
    inFile.close()

    print("Total words:", sum(d.values()))
    print("Unique words:", len(d))
    most = list(d.values())
    most.sort()
    for k in d:
        if d[k] == most[-1]:
            print("Most used word:  ", k, "  occurred", d[k], "times.")
    return d


def main():
    filename = input("Enter filename: ")
    word_count(filename)



if __name__ == '__main__':
    main()
