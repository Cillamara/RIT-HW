"""
CSCI-603 Lab 2: Ciphers

A program that encodes/decodes a message by applying a set of transformation operations.
The transformation operations are:
    shift - Sa[,n] changes letter at index a by moving it n letters fwd in the alphabet. A negative
        value for n shifts the letter backward in the alphabet.
    rotate - R[n] rotates the string n positions to the right. A negative value for n rotates the string
        to the left.
    duplicate - Da[,n] follows character at index a with n copies of itself.
    trade - Ta,b swap the places of the a-th and b-th characters.
    affine - Aa,b applies the affine cipher algorithm y = (ax + b) mod 26 using a and b as keys.

All indices numbers (the subscript parameters) are 0-based.

author: Liam Cui
"""
from IPython.utils.io import raw_print

def input_crypt():
    """
    Determines whether to encrypt or decrypt the message based on user input.
    Also allows the user to quit the program which breaks the infinite loop
    in the main function. The program loops until a valid input is entered.

    :return: 1 or 2 representing whether to encrypt of decrypt respectfully
    """

    print("Welcome to Ciphers!")
    while True:
        edp = input("What do you want to do: (E)ncrypt, (D)ecrypt or (Q)uit? ")
        if edp == "E" or edp == "e" or edp == "Encrypt" or edp == "encrypt":
            crypt = 1
            return crypt
        elif edp == "D" or edp == "d" or edp == "Decrypt" or edp == "decrypt":
            crypt = 2
            return crypt
        elif edp == "Q" or edp == "q" or edp == "Quit" or edp == "quit":
            quit()
        else:
            print("Invalid input, please try again.")

def input_msg():
    """
    Prompts user to enter a message. Then returns the message as a list
    of characters.

    :return: A list of characters from the input string
    """

    msg = input("Enter the message: ")
    msg_list = list(msg)
    return msg_list

def input_cipher(ed):
    """
    Prompts user to enter cipher instructions seperated by semicolons and no
    spaces. Then the function splits the individual transformation operations
    by the semicolons and places them as elements of a list which is returned.
    Then if decrypting, the function places the list in reverse order.
    Valid inputs for each transformation are the letter A, D, R, S, and T,
    followed by an integer. If the first letter is a T the instruction must be
    followed by a comma and then an integer to be valid. For letters D and S it
    is optional.

    :return: A list of transformation operations in order if encrypting and
        in reverse order if decrypting
    """

    raw_cipher = input("Enter the cipher instructions:")
    cipher = raw_cipher.split(";")
    if ed == 1: #Encryption Condition
        return cipher
    elif ed == 2: #Decryption Condition
        reversed_cipher = reversed(cipher)
        reversed_list = list(reversed_cipher)
        return reversed_list 

def direct_cipher(ed, cipher, msg):
    """
    Reads the cipher instructions and calls the encryption/decryption
    functions accordingly.

    :param ed: Encryption mode
    :param cipher: The list of cipher instructions
    :param msg: The message to be encrypted/decrypted as a list of characters
    :Output: Prints the encrypted/decrypted message as a string
    """

    new_msg = msg

    for i in range(len(cipher)):
        if cipher[i][0] == "S":
            ins = cipher[i].split(",")
            # Instructions: Transformation operations as a list
            new_msg = shift(ed, ins, new_msg)
        elif cipher[i][0] == "R":
            ins = cipher[i]
            # Instructions: Transformation operations as a string
            new_msg = rotate(ed, ins, new_msg)
        elif cipher[i][0] == "D":
            ins = cipher[i].split(",")
            # Instructions: Transformation operations as a list
            new_msg = duplicate(ed, ins, new_msg)
        elif cipher[i][0] == "T":
            ins = cipher[i].split(",")
            # Instructions: Transformation operations as a list
            new_msg = trade(ins, new_msg)
        elif cipher[i][0] == "A":
            ins = cipher[i].split(",")
            # Instructions: Transformation operations as a list
            new_msg = affine(ed, ins, new_msg)

    final_msg = "".join(new_msg) #converts list into a string
    print("\n" + final_msg) #prints the final string

def shift(ed, ins, new_msg):
    """
    Shifts a letter at a given position in a list of letters forward in the
    alphabet by a given value. If the shift takes the letter past the end of
    the alphabet, it will wrap around. Negative exponents shift the letter
    backward in the alphabet.

    :param ed: determines whether to encrypt or decrypt by a 1 or 2 value
        respectively
    :param ins: The cipher instruction string converted to a list by splitting
        at the comma (eg. cipher instruction string = S1,3 --> ins = [S1, 3])
    :param new_msg: The list of letters to be encrypted/decrypted
    :return: The list of letters with the specified letter shifted
    """

    shifted_msg = new_msg
    value = ord(new_msg[int(ins[0][1])]) - ord("A")  # letter as a number 0-25
    mag_pos = int(ins[0][1:])

    if len(ins) > 1:
        shift_value = int(ins[1]) % 26
    else:
        shift_value = 1

    if ed == 1: #Encryption
        shifted_msg[mag_pos] = chr(ord("A") + (value + shift_value) % 26)
        #encrypt function
    elif ed == 2: #Decryption
        shifted_msg[mag_pos] = chr(ord("A") + (value - shift_value) % 26)
        #decrypt function
    return shifted_msg

def rotate(ed, ins, new_msg):
    """
    Rotates the string one position to the right. So, TOPS→ R → STOP. A value
    can be provided which to determine the number of times this rotation is
    performed. If the value is negative, the rotation occurs in the opposite
    directions.

    :param ed: Encryption mode
    :param ins: Instruction for the cipher as a string
    :param new_msg: The list of letters to be encrypted/decrypted
    :return: The list of letters that have been rotated
    """

    rotated_msg = new_msg
    lr = len(rotated_msg)

    if ed == 1:  #Encryption: rotates right
        if len(ins) == 1:  #if instruction is just the letter R
            rotated_msg = rotated_msg[lr - 1:] + rotated_msg[:lr-1]
        elif ins[1] == "-": #ignores the negative sign then runs decrypted
            rotated_msg = (rotated_msg[int(ins[2:]):]
                           + rotated_msg[:int(ins[2:])])
        else:
            rotated_msg = (rotated_msg[lr - int(ins[1:]):]
                           + rotated_msg[:lr - int(ins[1:])])
    elif ed == 2: #decryption: rotates left
        if len(ins) == 1:  #if instruction is just the letter R
            rotated_msg = rotated_msg[1:] + rotated_msg[:1]
        elif ins[1] == "-": #ignores the negative sign then runs encrypted
            rotated_msg = (rotated_msg[lr - int(ins[2:]):]
                           + rotated_msg[:lr - int(ins[2:])])
        else:
            rotated_msg = (rotated_msg[int(ins[1:]):]
                           + rotated_msg[:int(ins[1:])])
    return rotated_msg

def duplicate(ed, ins, new_msg):
    """
    Duplicates a letter at a given position in a list of letters

    :param ed: Encryption mode
    :param ins: The cipher instruction string converted to a list by splitting
        at the comma (eg. cipher instruction string = D1,3 --> ins = [D1, 3])
    :param new_msg: The list of letters to be encrypted/decrypted
    :return: The list of letters that have been transformed
    """

    duplicated_msg = new_msg

    if ed == 1: #Encryption condition: Duplicates a letter
        if len(ins) == 1:
            duplicated_msg[int(ins[0][1:])] = (duplicated_msg[int(ins[0][1:])]
                                               * 2)
            duplicated_msg = list("".join(duplicated_msg))
        else:
            duplicated_msg[int(ins[0][1:])] = (duplicated_msg[int(ins[0][1:])]
                                               * (1 + int(ins[1])))
            duplicated_msg = list("".join(duplicated_msg))
    elif ed == 2: #Decryption condition: removes specified duplicates
        if len(ins) == 1:
            duplicated_msg[int(ins[0][1:])] = ""
        else:
            duplicated_msg[int(ins[0][1:]):int(ins[0][1:]) + int(ins[1])] = ""
    return duplicated_msg

def trade(ins, new_msg):
    """
    Swaps the letter at a given position of the list with the letter at the
    other given position

    :param ins: The cipher instruction string converted to a list by splitting
        at the comma (eg. cipher instruction string = T1,3 --> ins = [T1, 3])
    :param new_msg: The list of letters to be encrypted/decrypted
    :return: The list of letters that have 2 letters traded
    """

    traded_msg = new_msg

    traded_msg[int(ins[0][1:])], traded_msg[int(ins[1])] = \
        (traded_msg[int(ins[1])], traded_msg[int(ins[0][1:])])
    return traded_msg

def affine(ed, ins, new_msg):
    """
    Performs a substitution cipher where each character in the message is
    mapped to an integer value between 0-25 (e.g. a=0, b=1, ..., z=25),
    encrypted with a mathematical function and then converted back to the
    letter relating to its new integer value.

    :param ed: Encryption mode
    :param ins: The cipher instruction string converted to a list by splitting
        at the comma (eg. cipher instruction string = A1,3 --> ins = [A1, 3])
    :param new_msg: The list of letters to be encrypted/decrypted
    :return: The list of letters that have been substituted with the affine
        transformation
    """

    affined_msg = new_msg
    a = int(ins[0][1:])
    b = int(ins[1])

    if ed == 1: #encryption
        for i in range(len(affined_msg)):
            affined_msg[i] = chr((((a * (ord(affined_msg[i]) - ord("A"))
                                    + b) % 26) + ord("A")))
    elif ed == 2: #decryption
        for x in range(1, 26): #finds modular inverse of a as inv
            if (a * x) % 26 == 1: #if inverse is found, assigns inverse as inv
                inv = x
                break
        for i in range(len(affined_msg)): #inv * (y-b) mod 26
            affined_msg[i] = chr((inv * (ord(affined_msg[i]) - ord("A")
                                         - b) % 26) + ord("A"))
    return affined_msg

def main() -> None:
    """
    The main loop responsible for getting the input details from the user
    and printing in the standard output the results
    of encrypting or decrypting the message applying the transformations.
    This function is an infinite loop.

    :return: None
    """

    ed = input_crypt()
    msg = input_msg()
    cipher = input_cipher(ed)
    direct_cipher(ed, cipher, msg)

if __name__ == '__main__':
    while True:
        main()
