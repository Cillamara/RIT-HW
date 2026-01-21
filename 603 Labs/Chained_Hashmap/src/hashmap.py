"""
CSCI-603 Lab 7: Chained Hash Map
Author: Liam Cui
"""
import collections
from turtledemo.penrose import start
from typing import Any, Iterator, Callable
from pycount import word_count
from src.entry_node import EntryNode

class HashMap(collections.abc.Iterable):
    """
    A chained hash table that automatically expands and contracts (by a factor of 2)
    Terminology:
    number_of_buckets: the number of buckets/entries/chains in the hash
    table. It determines the range of legal table indices in the
    implementation. It changes during resizing.
    size:
    removed, from the table
    load_factor: ratio of size to number_of_buckets
    load_limit: specifies a range of which the table is too full or empty. When
    load_factor reaches load_limit, the number_of_buckets is doubled. When it
        gets under (1-load_limit), the number_of_buckets is cut in half.
    """
    __slots__ = ('_hash_func', '_bucket_size', '_load_limit', '_map', '_size', '_MIN_BUCKET_SIZE')
    _hash_func: any
    _bucket_size: int # table size
    _MIN_BUCKET_SIZE: int # the smallest allowed bucket_size
    _load_limit: float # max ratio of size/bucket_size allowed until rehash
    _map: tuple # the tuple the chained entries
    _size: int # the number of entries in the map

    def __init__(self, hash_func: Callable[[Any], int] = hash, initial_num_buckets: int = 100, load_limit: float = 0.75) -> None:
        """
        Create a new hash table of dummy entry nodes.
        :param initial_num_buckets: starting number_of_buckets
        :param load_limit: See class documentation above.
        :param hash_func: the hash function used to compute the entry's
            location into the hash table
        :return: None
        """

        self._bucket_size = initial_num_buckets
        self._hash_func = hash_func
        self._load_limit = load_limit
        self._map = tuple(EntryNode() for _ in range(initial_num_buckets))
        self._MIN_BUCKET_SIZE = initial_num_buckets
        self._size = 0

    def __iter__(self) -> Iterator[tuple[any, any]]:
        """
        Build an iterator.
        :return: an iterator for the current entries in the map
        """
        for bucket in self._map:
            node = bucket.get_link()
            while node is not None:
                yield node.get_key(), node.get_value()
                node = node.get_link()

    def __repr__(self) -> str:
        """
        Return a string with the content of the hash table and information a
        bout the hash table such as the table's capacity, size, current load
        factor and load limit.
        :return: a string with the content of the hash table
        """
        load_factor = self._size / self._bucket_size
        header = (
            f"Capacity: {self._bucket_size}, Size: {self._size}, "
            f"Load Factor: {load_factor:.1f}, Load Limit: {self._load_limit}\n\n"
            "Hash table\n"
            "---------\n")
        buckets = []
        for index, bucket in enumerate(self._map):
            buckets.append(f"{index}: {repr(bucket)}")
        return header + "\n".join(buckets)



    def __str__(self) -> str:
        """
        Return a string representation of the entries added to this map.
        The string will contain all the entries separated by comma and enclosed
        between curly braces.
        :return: a string representation of the entries added to this map
        """
        empty = []
        for key, value in self:
            empty.append(f'({key}, {value})')
        return '{' + ', '.join(empty) + '}'

    def __len__(self) -> int:
        """
        Return the number of entries in this map.
        :return: the number of entries in the map.
        """
        return self._size


    def add(self, key: any, value: any) -> None:
        """
        Insert a new entry into the hash table. However, if the key
        already exists, the value associated to that key is updated with the given value.
        Double the size of the table if its load_factor exceeds the load_limit.
        :param key: the key to add
        :param value: the value to add
        :return: None
        """
        index = self._hash_func(key) % self._bucket_size
        start = self._map[index]
        node = start
        # walk to the end
        while node.get_link() is not None:
            node = node.get_link()
            # if key exists replace value
            if node.get_key() == key:
                node.set_value(value)
                return
        new_node = EntryNode(key, value)
        node.set_link(new_node)
        self._size += 1
        # Rehashing/ comment out for answers
        if self._size / self._bucket_size > self._load_limit:
        self._bucket_size *= 2
        self.rehash()

    def contains(self, key: any) -> bool:
        """
        Is the given key in the hash table?
        :return: True if key or its equivalent has been added to this table
        """
        if self.get(key) is None:
            return False
        return True

    def get(self, key: any) -> any:
        """
        Return the value associated to the given key.
        :return: The value associated to the given key or None if key is not found
        """
        index = self._hash_func(key) % self._bucket_size
        node = self._map[index]
        while node is not None:
            if node.get_key() == key:
                return node.get_value()
            node = node.get_link()
        return None


    def imbalance(self) -> float:
        """
        Computes the imbalance of the hash table.
        :return: the average length of all non-empty chains minus 1, or 0 if hash table is empty
        """
        bucket_length = []
        for bucket in self._map:
            length = 0
            current = bucket.get_link()
            while current is not None:
                length += 1
                current = current.get_link()
            if length > 0:
                bucket_length.append(length)
        if len(bucket_length) > 0:
            return sum(bucket_length) / len(bucket_length) - 1
        return 0

    def remove(self, key: any) -> None:
        """
        Remove an object from the hash table.
        Resize the table if its load_factor has dropped below (1-load_limit).
        :pre: assumes the key's hashing and equality work
        :param key: the key to remove
        :return: None
        """
        if not self.contains(key):
            return

        index = self._hash_func(key) % self._bucket_size
        start = self._map[index]
        prev = start
        node = start.get_link()
        while node is not None:
            if node.get_key() == key:
                prev.set_link(node.get_link())
                self._size -= 1
                # Check if we need to shrink the table
                if (self._size / self._bucket_size < (1 - self._load_limit) and
                        self._bucket_size // 2 >= self._MIN_BUCKET_SIZE):
                    self._bucket_size = self._bucket_size // 2
                    self.rehash()
                return
            prev = node
            node = node.get_link()

    def rehash(self):
        old_entries = list(self)  # Collect all entries using the iterator
        self._map = tuple(EntryNode() for _ in range(self._bucket_size))
        self._size = 0
        for key, value in old_entries:
            self.add(key, value)

def improved_hash_formula(s: str) -> int:
    """
    Computes a hash value for string s using the formula given in problem the
    solving.
    """
    hash_value = 0
    for i, char in enumerate(s):
        hash_value += ord(char) * (31 ** i)
    return hash_value

def main():
    """
    main method
    """
    filename = input("Enter filename: ")
    word_dict = word_count(filename)
    word_map = HashMap(hash,1000, .75)
    for (key, value) in word_dict.items():
        word_map.add(key, value)
    print("imbalance:", word_map.imbalance())
    print(repr(word_map))

if __name__ == "__main__":
    main()




