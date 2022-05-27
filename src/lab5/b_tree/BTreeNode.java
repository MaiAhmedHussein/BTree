package ds2.lab5.b_tree;

import java.util.LinkedList;
import java.util.List;

public class BTreeNode<K extends Comparable<K>, V> implements IBTreeNode<K, V> {

    private List<K> keys;
    private List<V> values;
    private List<IBTreeNode<K, V>> children;
    private boolean isLeaf;
    private int numOfKeys;

    public BTreeNode() {
        this.keys = new LinkedList<>();
        this.values = new LinkedList<>();
        this.children = new LinkedList<>();
        this.numOfKeys = -1;
        this.isLeaf = false;
    }

    @Override
    public int getNumOfKeys() {
        return numOfKeys;
    }

    @Override
    public void setNumOfKeys(int numOfKeys) {
        this.numOfKeys = numOfKeys;
    }

    @Override
    public boolean isLeaf() {
        return isLeaf;
    }

    @Override
    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    @Override
    public List<K> getKeys() {
        return keys;
    }

    @Override
    public void setKeys(List<K> keys) {
        this.keys = new LinkedList<>(keys);
    }

    @Override
    public List<V> getValues() {
        return values;
    }

    @Override
    public void setValues(List<V> values) {
        this.values = new LinkedList<>(values);
    }

    @Override
    public List<IBTreeNode<K, V>> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<IBTreeNode<K, V>> children) {
        this.children = new LinkedList<>(children);
    }
}