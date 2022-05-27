package ds2.lab5.b_tree;

public interface IBTree<K extends Comparable<K>, V> {

    /**
     * Return the minimum degree of the given Btree .
     * The minimum degree of the Btree is sent as a parameter t to the constructor
     *
     * @return --
     */
    int getMinimumDegree();

    /**
     * @return Return the root o f the given Btree
     */
    IBTreeNode<K, V> getRoot();

    /**
     * Insert the given key in the Btree. If the key is already
     * in the Btree , ignore the call of this method
     *
     * @param key   --
     * @param value --
     */
    void insert(K key, V value);

    /**
     * Search for the given key in the BTree
     *
     * @param key --
     * @return --
     */
    V search(K key);

    /**
     * Delete the node with the given key from the Btree
     * Return true in case of success and false otherwise
     *
     * @param key --
     * @return --
     */
    boolean delete(K key);
}
