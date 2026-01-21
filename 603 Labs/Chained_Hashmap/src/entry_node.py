"""
CSCI-603 Lab 7: Chained Hash Map
Author: Liam Cui
"""

class EntryNode():
    __slots__ = ('_key', '_link', '_value')
    _key: any
    _value: any
    _link: 'EntryNode'
    def __init__(self, key: any = None, value: any = None, link: 'EntryNode' = None) -> None:
        """
        Create a new node that holds the given key and value. This node may be chained to another
        node in the Hash Map.
        """
        self._key = key
        self._value = value
        self._link = link

    def __repr__(self) -> str:
        """
        Return a string with the representation of the entry in this node and
        the entry of the node to which it is linked by the link.
            This function should not be called for a circular list.
        :return: the entry's representation. The format of the string is as follows:
        """
        repr_list = []
        node = self
        while node is not None:
            if node.get_key() is not None:  # Only include nodes with actual keys
                repr_list.append(f"('{node.get_key()}',{node.get_value()})")
            node = node.get_link()  # Move to next node
        if len(repr_list) > 0:
            return " -> ".join(repr_list) + " -> None"
        else: return "None"

    def __str__(self) -> str:
        """
        Return the string representation of the entry in this node.
        :return: the entry's string representation. The format is as follows:
        """
        return "(" + str(self._key) + "," + str(self._value) + ")"

    def get_key(self) -> any:
        """
        Returns the node's key
        :return: the node's key
        """
        return self._key

    def get_link(self) -> 'EntryNode':
        """
        Returns the node's link
        :return: the node's link
        """
        return self._link

    def get_value(self) -> any:
        """
        Returns the node's value
        :return: the node's value
        """
        return self._value

    def set_link(self, link: 'EntryNode') -> None:
        """
        Link this node with the given node
        """
        self._link = link

    def set_value(self, value: any) -> None:
        """
        Update the Node's value'
        """
        self._value = value
