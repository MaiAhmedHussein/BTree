import java.util.ArrayList;
import java.util.List;

public class BTreeNode<K extends Comparable<K>, V> implements IBTreeNode<K, V> {
    private int numOfKeys;
    private boolean isLeaf = false;

    //the maximum size of it 2*t-1
    private List<K> keys = new ArrayList<>();
    private List<V> values= new ArrayList<>();
    //Number of children of a node is equal to the number of keys in it plus 1
    private List<IBTreeNode<K, V>> children= new ArrayList<>();



    @Override
    public int getNumOfKeys() {
        return this.keys.size();
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
        this.keys = keys;
    }

    @Override
    public List<V> getValues() {
        return values;
    }

    @Override
    public void setValues(List<V> values) {
        this.values = values;
    }

    @Override
    public List<IBTreeNode<K, V>> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<IBTreeNode<K, V>> children) {
        this.children = children;
    }


    public void addToValues(V value) {
        this.values.add(value);
    }

    public void addToKeys(K key) {
        this.keys.add(key);
    }

    public void addToChildren(BTreeNode node) {
        this.children.add(node);
    }

}

