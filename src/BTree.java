import javax.management.RuntimeErrorException;
import java.util.ArrayList;
import java.util.List;

public class BTree <K extends Comparable<K>, V> implements IBTree<K,V> {
    private  int minimumDegree;
    private int minKeys;
    private int maxKeys;
    private IBTreeNode<K, V> root;

    int t;
   BTree(int minimumDegree){
       if (minimumDegree < 1){
           throw new RuntimeErrorException(new Error());
       }
       this.minimumDegree = minimumDegree;
       //Every node except root must contain so minkeys = t-1 keys. The root may contain minimum 1 key.
       minKeys = minimumDegree - 1;
       //max no. of keys =2*t-1
       maxKeys = minimumDegree * 2 - 1; //max number of keys allowed in one node
   }

    @Override
    public int getMinimumDegree() {
        return minimumDegree;
    }

    @Override
    public IBTreeNode<K, V> getRoot() {
        return root;
    }

    @Override
    public void insert(K key, V value) {
     //search if key exist before
        //return
        if(getRoot()==null){
            root = new BTreeNode();
            List<K> keys =new ArrayList<>();
            keys.add(key);
            List<V> values= new ArrayList<>();
            values.add(value);
            root.setKeys(keys);
            root.setValues(values);
            root.setLeaf(true);
            root.setNumOfKeys(1);
            return;
        }else{
            // in the node reach maximum number of keys do need to create new node
            if(getRoot().getNumOfKeys()==maxKeys){

            }

        }
    }

    @Override
    public V search(K key) {
        return null;
    }

    @Override
    public boolean delete(K key) {
        return false;
    }
}
