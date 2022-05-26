
public interface IBTree<K extends Comparable<K>, V> {
    //minimum degree ‘t’
    public int getMinimumDegree();

    public IBTreeNode<K, V> getRoot();

    public void insert(K key, V value);

    public V search(K key);

    public boolean delete(K key);

}
