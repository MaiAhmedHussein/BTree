package lab5.b_tree;

public interface IBTree<K extends Comparable<K>, V> {

    int getMinimumDegree();

    /**
     * Return the root of the given Btree.
     *
     * @return
     */
    IBTreeNode<K, V> getRoot();

    /**
     * Insert the given key in the Btree. If the key is already in the Btree, ignore the call of this method.
     *
     * @param key
     * @param value
     */
    void insert(K key, V value);

    /**
     * Search for the given key in the BTree.
     *
     * @param key
     * @return
     */
    V search(K key);

    /**
     * Delete the node with the given key from the Btree.
     * Return true in case of success and false otherwise.
     *
     * @param key
     * @return
     */
    boolean delete(K key);
}
