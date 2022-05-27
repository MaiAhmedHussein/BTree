package lab5.b_tree;

import java.util.List;


public interface IBTreeNode<K extends Comparable<K>, V> {

    /**
     * @return the numOfKeys return number of keys in this node.
     */
    int getNumOfKeys();

    /**
     * @param numOfKeys --
     */
    void setNumOfKeys(int numOfKeys);

    /**
     * @return isLeaf if the node is leaf or not.
     */
    boolean isLeaf();

    /**
     * @param isLeaf --
     */
    void setLeaf(boolean isLeaf);

    /**
     * @return the keys return the list of keys of the given node.
     */
    List<K> getKeys();

    /**
     * @param keys the keys to set
     */
    void setKeys(List<K> keys);

    /**
     * @return the values return the list of values for the given node.
     */
    List<V> getValues();

    /**
     * @param values the values to set
     */
    void setValues(List<V> values);

    /**
     * @return the children return the list of children for the given node.
     */
    List<IBTreeNode<K, V>> getChildren();

    /**
     * @param children the children to set
     */
    void setChildren(List<IBTreeNode<K, V>> children);

}
