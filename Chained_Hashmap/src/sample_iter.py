"""
A minimal example showing two ways to write an iterator in Python 3

Both classes iterate over the integers from 1 to n.
author: CS RIT
"""


from collections.abc import Iterable, Iterator

class IntRange1( Iterable ):

    __slots__ = "n"

    def __init__( self, n ):
        self.n = n

    def __iter__( self ):
        return IntRange1.Iter( self.n )

    class Iter( Iterator ):

        __slots__ = "i", "stop_value"

        def __init__( self, n ):
            self.i = 0
            self.stop_value = n

        def __next__( self ):
            if self.i >= self.stop_value:
                raise StopIteration()
            self.i += 1
            return self.i

class IntRange2( Iterable ):

    __slots__ = "n"

    def __init__( self, n ):
        self.n = n

    def __iter__( self ):
        i = 1
        while i <= self.n:
            yield i
            i += 1

def test( ):
    for i in IntRange1( 3 ):
        print( i, end=" " )
    print()

    for i in IntRange1( 7 ):
        print( i, end=" " )
    print()

    print( list( IntRange1( 10 ) ) )

    for i in IntRange2( 3 ):
        print( i, end=" " )
    print()

    for i in IntRange2( 7 ):
        print( i, end=" " )
    print()

    print( list( IntRange2( 10 ) ) )


if __name__ == '__main__':
    test( )
