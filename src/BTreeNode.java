import java.util.List;

public class BTreeNode<K extends Comparable<K>, V> implements IBTreeNode<K, V> {
    //Every node except root must contain at least t-1 keys. The root may contain minimum 1 key.
    //the maximum size of it 2*t-1
    private List<K> keys;
    private List<V> values;
    //Number of children of a node is equal to the number of keys in it plus 1
    private List<IBTreeNode<K, V>> children;


    @Override
    public int getNumOfKeys() {
        return keys.size();
    }

    @Override
    public void setNumOfKeys(int numOfKeys) {

    }

    @Override
    public boolean isLeaf() {
        return children.size() == 0;
    }

    @Override
    public void setLeaf(boolean isLeaf) {
        children.clear();
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

