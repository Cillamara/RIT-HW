import unittest

from hashmap import HashMap


class MyTestCase(unittest.TestCase):
    """
    Unit class to verify the correctness of the hash map.
    The hash map's state must be a black box, the
    tests in this class should focus on verifying that the hash map produces
    the right responses to queries.
    We will use the str and repr methods to verify that the hash map ends up with
    the expected state.

    The name of the test methods indicate their order of execution.
    """

    __slots__ = "_the_map"
    _the_map: HashMap

    def setUp(self):
        """
        This method is called before calling any test method.
        """
        # the hash map will contain 10 buckets and load_limit 75 (default value)
        # it will use the built-in hash function by default
        self._the_map = HashMap(initial_num_buckets=10)

    @staticmethod
    def print_map(the_map: HashMap):
        print("Printing hash map's entries:")
        for key, value in the_map:  # uses the iter method
            print(key, value)
        print()

    def test_01_init(self):
        """
        Unit test to verify the hash map's constructor.
        """
        self.assertEqual(len(self._the_map), 0)
        self.assertEqual(str(self._the_map), '{}')

        # trying to create a hash map with an illegal
        # number of buckets (buckets < MIN_BUCKETS)
        tiny_map = HashMap(initial_num_buckets=5)
        self.assertEqual(len(tiny_map), 0)
        self.assertEqual(str(tiny_map), '{}')
        print()
        print(repr(tiny_map))

    def test_02_basics(self):
        """
        Unit test to verify basic operations:
            add (new insertion and update)
            contains
            remove
            get
            iterate over the entries
        """
        self._the_map.add("to", 1)
        self._the_map.add("do", 1)
        self._the_map.add("is", 1)
        self._the_map.add("to", 2)  # updating an entry's value
        self._the_map.add("be", 1)
        self.assertEqual(len(self._the_map), 4)

        print()
        MyTestCase.print_map(self._the_map)
        print()
        print(repr(self._the_map))  # not rehashing yet, the load factor should be 40%

        self.assertEqual(self._the_map.contains("to"), True)
        self.assertEqual(self._the_map.get("to"), 2)
        self._the_map.remove("to")
        self.assertEqual(self._the_map.contains("to"), False)
        self.assertEqual(len(self._the_map), 3)

        print()
        MyTestCase.print_map(self._the_map)

    def test_03_rehashing(self):
        """
        Unit test automatic resizing when load factor exceeds limits:
        Insert 3 items
        rehash
        remove 2 items
        rehash
        """
        hm = HashMap(initial_num_buckets=4, load_limit=0.75)

        # trigger rehash (capacity doubles to 8)
        hm.add("a", 1)
        hm.add("b", 2)
        hm.add("c", 3)
        self.assertEqual(hm._bucket_size, 8)
        self.assertEqual(len(hm), 3)

        #trigger shrink (capacity halves to 4)
        hm.remove("a")
        hm.remove("b")
        self.assertEqual(hm._bucket_size, 4)
        self.assertEqual(len(hm), 1)

if __name__ == '__main__':
    unittest.main()
