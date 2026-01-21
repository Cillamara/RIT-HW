import unittest

from entry_node import EntryNode


class MyTestCase(unittest.TestCase):
    __slots__ = "_node1", "_node2"
    _node1: EntryNode
    _node2: EntryNode

    def setUp(self):
        """
        This method is called before calling any test method
        """
        self._node1 = EntryNode('one', 1)
        self._node2 = EntryNode('two', 2, self._node1)

    def test_init(self):
        """
        Unit test to verify the EntryNode's constructor and the setter methods
        """
        self.assertEqual(self._node1.get_key(), 'one')
        self.assertEqual(self._node1.get_value(), 1)
        self.assertEqual(self._node1.get_link(), None)
        self._node1.set_value(11)
        self.assertEqual(self._node1.get_value(), 11)
        self.assertEqual(self._node1.get_key(), 'one')

        self.assertEqual(self._node2.get_key(), 'two')
        self.assertEqual(self._node2.get_value(), 2)
        self.assertEqual(self._node2.get_link(), self._node1)
        node3 = EntryNode('three', 3)
        self._node2.set_link(node3)
        self.assertNotEqual(self._node2.get_link(), self._node1)
        self.assertEqual(self._node2.get_link(), node3)

    def test_str(self):
        """
        Unit test to verify the EntryNode's __str__ method
        """
        self.assertEqual("(one,1)", str(self._node1))
        self.assertEqual("(two,2)", str(self._node2))

    def test_repr(self):
        """
        Unit test to verify the EntryNode's __repr__ method
        """
        self.assertEqual("('one',1) -> None", repr(self._node1))
        self.assertEqual("('two',2) -> ('one',1) -> None", repr(self._node2))


if __name__ == '__main__':
    unittest.main()
