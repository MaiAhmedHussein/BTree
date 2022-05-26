import java.util.List;

public interface IBTreeNode<K extends Comparable<K>, V> {
    public int getNumOfKeys();

    public void setNumOfKeys(int numOfKeys);

    public boolean isLeaf();

    public void setLeaf(boolean isLeaf);

    public List<K> getKeys();

    public void setKeys(List<K> keys);

    public List<V> getValues();

    public void setValues(List<V> values);

    public List<IBTreeNode<K, V>> getChildren();

    public void setChildren(List<IBTreeNode<K, V>> children);

}



