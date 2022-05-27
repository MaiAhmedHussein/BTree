package lab5.b_tree;

import javax.management.RuntimeErrorException;

import java.util.LinkedList;
import java.util.List;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {


    private final int maxKeys;
    private IBTreeNode<K, V> root;
    private final int minimumDegree;


    public BTree(int minimumDegree) {
        this.root = null;
        if (minimumDegree < 1) {
            throw new RuntimeErrorException(new Error());
        } else {
            this.minimumDegree = minimumDegree;

            this.maxKeys = minimumDegree * 2 - 1;
        }
    }

    @Override
    public int getMinimumDegree() {
        return this.minimumDegree;
    }

    @Override
    public IBTreeNode<K, V> getRoot() {
        return this.root;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void insert(K key, V value) {
        if (key == null || value == null){ //invalid input
            throw new RuntimeErrorException(new Error());
        }
        if (search(key) != null){ //already exists
            return;
        }
        if (this.root == null){
            //tree is empty
            IBTreeNode<K, V> newRoot = new BTreeNode<>();
            List<K> keys = new LinkedList<>();
            List<V> values = new LinkedList<>();
            keys.add(key);
            values.add(value);
            List<IBTreeNode<K, V>> children = new LinkedList<>();
            newRoot.setKeys(keys);
            newRoot.setValues(values);
            newRoot.setNumOfKeys(keys.size());
            newRoot.setChildren(children);
            newRoot.setLeaf(true); //the root of the tree is also a leaf
            this.root = newRoot;
            return;
        }
        //to get the position where to insert the new key
        IBTreeNode<K, V> insertInto = this.getInsertionPosition(this.root, key);
        List<K> keys = insertInto.getKeys();
        int i;
        //if the inserted value is greater than the key in the list
        //then find the right to place to insert into inside the node
        for (i = 0 ; i < insertInto.getNumOfKeys(); i++){
            //find the right to place to insert into inside the node
            if (key.compareTo(keys.get(i)) < 0){
                break;
            }
        }
        keys.add(i, key);
        insertInto.getValues().add(i, value);
        insertInto.setNumOfKeys(keys.size());
    }

    public IBTreeNode<K, V> getInsertionPosition(IBTreeNode<K, V> node, K key) {
        // If root is full, then tree grows in height
        //so we need to split it
        if (node.getNumOfKeys() == this.maxKeys) {
            this.splitRoot(node);
            node = this.root;
        }
        while (!node.isLeaf()) {
            List<K> keys = node.getKeys();

            for (int i = 0; i < keys.size(); ++i) {
                K k = keys.get(i);
                if (key.compareTo(k) < 0) {
                    //as the key inserted smaller than the key in the list
                    //traverse through left child
                    List<IBTreeNode<K, V>> children = node.getChildren();
                    IBTreeNode<K, V> child = children.get(i);
                    if (child.getNumOfKeys() == maxKeys) {
                        //split if left child is full
                        child = split(node, child, key);
                    }
                    node = child;
                    break;
                } else if (i == keys.size() - 1) {
                    //traverse through the most right child
                    List<IBTreeNode<K, V>> children = node.getChildren();
                    IBTreeNode<K, V> child = children.get(i + 1);
                    if (child.getNumOfKeys() == maxKeys) {
                        //split if the most right child is full
                        child = split(node, child, key);
                    }
                    node = child;
                    break;
                }
            }

        }

        return node;
    }

    //where node is the parent of the child
    public IBTreeNode<K, V> split(IBTreeNode<K, V> node, IBTreeNode<K, V> child, K key) {
        IBTreeNode<K, V> leftSplit = new BTreeNode<>();
        IBTreeNode<K, V> rightSplit = new BTreeNode<>();

        List<K> keys = child.getKeys();
        List<V> values = child.getValues();
        List<IBTreeNode<K, V>> children = child.getChildren();

        List<K> newKeys = new LinkedList<>();
        List<V> newValues = new LinkedList<>();
        List<IBTreeNode<K, V>> newChildren = new LinkedList<>();

        if (child.isLeaf()) {
            //assign the new leaves
            leftSplit.setLeaf(true);
            rightSplit.setLeaf(true);
        }
        //for the left side loop until the middle
        int i;
        for (i = 0; i < child.getNumOfKeys() / 2; ++i) {
            newKeys.add(keys.get(i));
            newValues.add(values.get(i));
            if (!child.isLeaf()) {
                newChildren.add(children.get(i));
            }
        }

        if (!child.isLeaf()) {
            newChildren.add(children.get(i));
        }
        leftSplit.setKeys(newKeys);
        leftSplit.setValues(newValues);
        leftSplit.setChildren(newChildren);
        leftSplit.setNumOfKeys(newKeys.size());

        //updating for the new root
        newKeys = node.getKeys();
        newValues = node.getValues();
        newChildren = node.getChildren();
        K midKey = (keys.get(i));

        int j;
        for ( j = 0 ; j < newKeys.size(); j++){
            if (keys.get(i).compareTo(newKeys.get(j)) < 0){
                break;
            }
        }

            newKeys.add(j, midKey);
        newValues.add(j, values.get(i));
        newChildren.remove(j);
        newChildren.add(j, leftSplit);
        newChildren.add(j + 1, rightSplit);
        node.setKeys(newKeys);
        node.setValues(newValues);
        node.setChildren(newChildren);
        node.setNumOfKeys(newKeys.size());

        //initialize for the right side

        newKeys = new LinkedList<>();
        newValues = new LinkedList<>();
        newChildren = new LinkedList<>();

        ++i;
        for (; i < child.getNumOfKeys(); ++i) {
            newKeys.add(keys.get(i));
            newValues.add(values.get(i));
            if (!child.isLeaf()) {
                newChildren.add(children.get(i));
            }
        }

        if (!child.isLeaf()) {
            newChildren.add(children.get(i));
        }
        rightSplit.setKeys(newKeys);
        rightSplit.setValues(newValues);
        rightSplit.setChildren(newChildren);
        rightSplit.setNumOfKeys(newKeys.size());

        //if the key greater than the midKey so return the right side
        if (key.compareTo(midKey) > 0) {
            return rightSplit;
        } else {
            return leftSplit;
        }
    }

    public void splitRoot(IBTreeNode<K, V> node) {
        List<K> keys = node.getKeys();
        List<V> values = node.getValues();
        List<IBTreeNode<K, V>> children = node.getChildren();

        IBTreeNode<K, V> newRoot = new BTreeNode<>();
        IBTreeNode<K, V> leftSplit = new BTreeNode<>();
        IBTreeNode<K, V> rightSplit = new BTreeNode<>();

        List<K> newKeys = new LinkedList<>();
        List<V> newValues = new LinkedList<>();
        List<IBTreeNode<K, V>> newChildren = new LinkedList<>();

        if (node.isLeaf()) {
            leftSplit.setLeaf(true);
            rightSplit.setLeaf(true);
        }
        //looping through first half of the list to be left side of the split

        int i;
        for (i = 0; i < node.getNumOfKeys() / 2; ++i) {
            newKeys.add(keys.get(i));
            newValues.add(values.get(i));
            if (!node.isLeaf()) {
                newChildren.add(children.get(i));
            }
        }

        if (!node.isLeaf()) {
            newChildren.add(children.get(i));
        }
        //and update its keys, values, children and number of keys
        leftSplit.setKeys(newKeys);
        leftSplit.setValues(newValues);
        leftSplit.setChildren(newChildren);
        leftSplit.setNumOfKeys(newKeys.size());

        // initialize again the newKeys, newValues,newChildren to be used for the newRoot

        newKeys = new LinkedList<>();
        newValues = new LinkedList<>();
        newChildren = new LinkedList<>();

        //take the middle value and make it the newRoot
        //update for the newRoot all its keys, values, children and number of keys
        newKeys.add(keys.get(i));
        newValues.add(values.get(i));
        newChildren.add(leftSplit);
        newChildren.add(rightSplit);
        newRoot.setKeys(newKeys);
        newRoot.setValues(newValues);
        newRoot.setChildren(newChildren);
        newRoot.setNumOfKeys(newKeys.size());

        // initialize again the newKeys, newValues,newChildren to be used for the right side
        newKeys = new LinkedList<>();
        newValues = new LinkedList<>();
        newChildren = new LinkedList<>();

        //continue looping after the middle to be the right side of the new root
        ++i;
        for (; i < node.getNumOfKeys(); ++i) {
            newKeys.add(keys.get(i));
            newValues.add(values.get(i));
            if (!node.isLeaf()) {
                newChildren.add(children.get(i));
            }
        }

        if (!node.isLeaf()) {
            newChildren.add(children.get(i));
        }

        rightSplit.setKeys(newKeys);
        rightSplit.setValues(newValues);
        rightSplit.setChildren(newChildren);
        rightSplit.setNumOfKeys(newKeys.size());
        //at the end this newRoot will be our root
        this.root = newRoot;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public V search(K key) {
        if (key == null) {
            return null;
        }
        IBTreeNode<K, V> nodeSearching = search(root, key);
        // Not found unfortunately
        if (nodeSearching == null) return null;
        // yes, found
        if (nodeSearching.getKeys().contains(key))
            return nodeSearching.getValues().get(nodeSearching.getKeys().indexOf(key));
        return null;
    }

    private IBTreeNode<K, V> search(IBTreeNode<K, V> root, K key) {
        if (root == null) return null;

        int i = 0;
        while (i < root.getNumOfKeys() && key.compareTo(root.getKeys().get(i)) > 0) {
            i += 1;
        }
        if (i < root.getNumOfKeys() && key == root.getKeys().get(i)) {
            return root;
        } else if (root.isLeaf()) {
            return null;
        }
        return search(root.getChildren().get(i), key);
    }

    @Override
    public boolean delete(K key) {
        // If the key not in the B-tree
        if (search(key) == null) return false;
        delete(root, key);
        if (root.getKeys().isEmpty() && !root.isLeaf()) {
            root = root.getChildren().get(0);
        }
        return true;
    }

    private void delete(IBTreeNode<K, V> root, K key) {

        // Three cases for deletion
        // CASE I: Key is in node x, and x is a leaf
        if (root.isLeaf()) {

            List<K> xKeys = root.getKeys();
            List<V> xValues = root.getValues();

            // Delete the key from x node
            int indexOfKey = xKeys.indexOf(key);
            xKeys.remove(key);
            xValues.remove(indexOfKey);

            root.setKeys(xKeys);
            root.setValues(xValues);
            return;
        }

        // CASE II: Key is in the node x, and x is an internal node
        // Three subcases
        if (foundKeyInCurrentNode(root, key)) {
            internalX(root, key);
            return;
        }

        // CASE II: Key isn't in internal node x, but in child x.ci
        internalXExternalK(root, key);
    }

    // For CASE II Deletion
    private void internalXExternalK(IBTreeNode<K, V> root, K key) {
        // Determine the root x.ci of the appropriate subtree contains k
        IBTreeNode<K, V> subtree = getSubtreeCloseK(root, key);

        // if x.ci has only t-1 keys, execute 3a or 3b
        if (subtree != null && subtree.getNumOfKeys() < minimumDegree) {
            IBTreeNode<K, V> sibling = getImmediateSibling(root, subtree);
            if (sibling != null) {
                // CASE 3a:
                // when x.ci has immediate sibling with at least t keys
                // give x.ci an extra key by moving a key from x down to x.ci
                // move a key from x.ci immediate left or right sibling up to x
                moveKeys(root, subtree, sibling);
                delete(subtree, key);
            } else {
                // CASE 3b:
                // when x.ci and both of x.ci immediate siblings have (t-1) keys
                // merge x.ci with one of its siblings which involves moving
                // a key from x down into the new merged node to become the median key for this node
                int index = getIndexOfAnySibling(root, subtree);
                List<IBTreeNode<K, V>> xChildren = root.getChildren();

                // right or left sibling
                boolean rightSibling = index > 0 && xChildren.get(index - 1) == subtree;
                sibling = xChildren.get(index);

                K medianK;
                if (rightSibling) {
                    medianK = root.getKeys().get(index - 1);
                    merge(root, medianK, subtree, sibling);
                    delete(subtree, key);
                } else {
                    medianK = root.getKeys().get(index);
                    merge(root, medianK, sibling, subtree);
                    delete(sibling, key);
                }
            }
        } else {
            // recursively move down to the merged node to delete the target tree
            assert subtree != null;
            delete(subtree, key);
        }
    }

    private int getIndexOfAnySibling(IBTreeNode<K, V> root, IBTreeNode<K, V> subtree) {
        List<IBTreeNode<K, V>> xChildren = root.getChildren();
        for (int i = 0; i < xChildren.size(); i++) {
            if (xChildren.get(i) == subtree) {
                // right sibling
                if (i != xChildren.size() - 1) return i + 1;
                // left sibling
                return i - 1;
            }
        }
        return 0;
    }

    private void moveKeys(IBTreeNode<K, V> root, IBTreeNode<K, V> xc, IBTreeNode<K, V> sibling) {
        // right sibling or left sibling
        boolean rightSibling = false;
        int xcIndex = 0;

        List<IBTreeNode<K, V>> xChildren = root.getChildren();
        for (int i = 0; i < xChildren.size(); i++) {
            if (xChildren.get(i) == xc) {
                xcIndex = i;
                rightSibling = i != xChildren.size() - 1 && xChildren.get(i + 1) == sibling;
                break;
            }
        }

        if (rightSibling) {
            // move value from x to x.ci
            List<K> xKeys = root.getKeys();
            List<V> xValues = root.getValues();
            K xKeyToMovedToXC = xKeys.get(xcIndex);
            V xValueToMovedToXC = xValues.get(xcIndex);
            List<K> xcKeys = xc.getKeys();
            List<V> xcValues = xc.getValues();
            xcKeys.add(xKeyToMovedToXC);
            xcValues.add(xValueToMovedToXC);
            xc.setKeys(xcKeys);
            xc.setValues(xcValues);

            // move value from right sibling to the x
            List<K> siblingsKeys = sibling.getKeys();
            List<V> siblingsValues = sibling.getValues();
            // right sibling first index to be moves
            K siblingKMoveToX = siblingsKeys.remove(0);
            V siblingVMoveToX = siblingsValues.remove(0);
            xKeys.set(xcIndex, siblingKMoveToX);
            xValues.set(xcIndex, siblingVMoveToX);
            root.setKeys(xKeys);
            root.setValues(xValues);
            sibling.setKeys(siblingsKeys);
            sibling.setValues(siblingsValues);

            // move pointers of children if not root from sibling to x.ci
            if (!xc.isLeaf()) {
                List<IBTreeNode<K, V>> siblingChildren = sibling.getChildren();
                List<IBTreeNode<K, V>> xcChildren = xc.getChildren();
                xcChildren.add(siblingChildren.remove(0));
                sibling.setChildren(siblingChildren);
                xc.setChildren(xcChildren);
            }

        } else { // left Sibling __ The same steps as right

            xcIndex -= 1;
            // move value from x to x.ci
            List<K> xKeys = root.getKeys();
            List<V> xValues = root.getValues();
            K xKeyToMovedToXC = xKeys.get(xcIndex);
            V xValueToMovedToXC = xValues.get(xcIndex);
            List<K> xcKeys = xc.getKeys();
            List<V> xcValues = xc.getValues();
            xcKeys.add(0, xKeyToMovedToXC);
            xcValues.add(0, xValueToMovedToXC);
            xc.setKeys(xcKeys);
            xc.setValues(xcValues);

            // move value from right sibling to the x
            List<K> siblingsKeys = sibling.getKeys();
            List<V> siblingsValues = sibling.getValues();
            // right sibling first index to be moves
            K siblingKMoveToX = siblingsKeys.remove(siblingsKeys.size() - 1);
            V siblingVMoveToX = siblingsValues.remove(siblingsValues.size() - 1);
            xKeys.set(xcIndex, siblingKMoveToX);
            xValues.set(xcIndex, siblingVMoveToX);
            root.setKeys(xKeys);
            root.setValues(xValues);
            sibling.setKeys(siblingsKeys);
            sibling.setValues(siblingsValues);

            // move pointers of children if not root from sibling to x.ci
            if (!xc.isLeaf()) {
                List<IBTreeNode<K, V>> siblingChildren = sibling.getChildren();
                List<IBTreeNode<K, V>> xcChildren = xc.getChildren();
                xcChildren.add(0, siblingChildren.remove(siblingChildren.size() - 1));
                sibling.setChildren(siblingChildren);
                xc.setChildren(xcChildren);
            }
        }

    }

    private IBTreeNode<K, V> getImmediateSibling(IBTreeNode<K, V> root, IBTreeNode<K, V> subtree) {
        List<IBTreeNode<K, V>> xChildren = root.getChildren();
        for (int i = 0; i < xChildren.size(); i++) {
            if (xChildren.get(i) == subtree) {
                // Check left sibling with at least t keys
                if (i > 0 && xChildren.get(i - 1).getNumOfKeys() >= minimumDegree) {
                    return xChildren.get(i - 1);
                }

                // Check right sibling with at least t keys
                if (i < xChildren.size() - 1 && xChildren.get(i + 1).getNumOfKeys() >= minimumDegree) {
                    return xChildren.get(i + 1);
                }
            }
        }
        return null;
    }

    private IBTreeNode<K, V> getSubtreeCloseK(IBTreeNode<K, V> root, K key) {
        List<K> keys = root.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            if (key.compareTo(keys.get(i)) <= 0)
                return root.getChildren().get(i);
            if (i == keys.size() - 1)
                return root.getChildren().get(i + 1);
        }
        return null;
    }


    // For CASE II Deletion
    private void internalX(IBTreeNode<K, V> root, K key) {
        // Three subcases for CASE II

        // index of value which equivalent to key
        int index = root.getKeys().indexOf(key);

        // CASE 2a:
        // child y that precedes k in node x has at least t keys, then
        // find the predecessor k` of k in the subtree rooted at y
        IBTreeNode<K, V> predecessor = getPredecessor(root, key);
        if (predecessor != null && predecessor.getNumOfKeys() >= minimumDegree) {
            K predK = getPredecessorK(predecessor);
            V predV = getPredecessorV(predecessor);

            // delete the predecessor key
            delete(predecessor, predK);

            // make predecessor in the place of the deleted key
            List<K> xKeys = root.getKeys();
            List<V> xValues = root.getValues();
            xKeys.set(index, predK);
            xValues.set(index, predV);
            root.setKeys(xKeys);
            root.setValues(xValues);
            return;
        }

        // CASE 2b:
        // child y has fewer than t keys, then symmetrically examine
        // the child z that follow k in node x, if z has at least t keys, then
        // find the successor k` of k in the subtree rooted at z
        IBTreeNode<K, V> successor = getSuccessor(root, key);
        if (successor != null && successor.getNumOfKeys() >= minimumDegree) {
            K sucK = getSuccessorK(successor);
            V sucV = getSuccessorV(successor);

            // delete the successor key
            delete(successor, sucK);

            // make successor in the place of the deleted key
            List<K> xKeys = root.getKeys();
            List<V> xValues = root.getValues();
            xKeys.set(index, sucK);
            xValues.set(index, sucV);
            root.setKeys(xKeys);
            root.setValues(xValues);
            return;
        }

        // CASE 2c:
        // when both y, z have only (t-1) keys, merge k and all of z into y
        // x loses both k and the pointer to z, and now y contains (2t-1) keys
        // free z and recursively delete k from y
        assert predecessor != null;
        assert successor != null;
        merge(root, key, predecessor, successor);
        delete(predecessor, key);
    }

    private void merge(IBTreeNode<K, V> root, K key, IBTreeNode<K, V> y, IBTreeNode<K, V> z) {
        // point to the value of key in values list
        int index = root.getKeys().indexOf(key);

        // keys of y to add to them the keys of key, and z
        List<K> yKeys = y.getKeys();
        List<V> yValues = y.getValues();
        yKeys.add(key);
        yValues.add(root.getValues().get(index));
        yKeys.addAll(z.getKeys());
        yValues.addAll(z.getValues());

        // set y keys, and values
        y.setKeys(yKeys);
        y.setValues(yValues);

        // initialize the pointers to z children when added to y if z has
        if (!z.isLeaf()) {
            List<IBTreeNode<K, V>> yChildren = y.getChildren();
            yChildren.addAll(z.getChildren());
            y.setChildren(yChildren);
        }

        // Clean after that mess __ remove k from x and the pointer to z
        List<K> xKeys = root.getKeys();
        List<V> xValues = root.getValues();
        xKeys.remove(key);
        xValues.remove(index);
        root.setKeys(xKeys);
        root.setValues(xValues);
        List<IBTreeNode<K, V>> xChildren = root.getChildren();
        xChildren.remove(xChildren.get(index + 1));
        root.setChildren(xChildren);

        // :)))
    }

    private V getSuccessorV(IBTreeNode<K, V> successor) {
        // Successor is the first key
        if (successor.isLeaf())
            return successor.getValues().get(0);
        return getSuccessorV(successor.getChildren().get(0));
    }

    private K getSuccessorK(IBTreeNode<K, V> successor) {
        // Successor is the first key
        if (successor.isLeaf())
            return successor.getKeys().get(0);
        return getSuccessorK(successor.getChildren().get(0));
    }

    private IBTreeNode<K, V> getSuccessor(IBTreeNode<K, V> root, K key) {
        List<K> keys = root.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            if (key.compareTo(keys.get(i)) == 0)
                return root.getChildren().get(i + 1);
        }
        return null;
    }

    private V getPredecessorV(IBTreeNode<K, V> predecessor) {
        // Predecessor is the last key
        int preIndex;
        // if it's a leaf, then the predecessor doesn't have children
        if (predecessor.isLeaf()) {
            preIndex = predecessor.getKeys().size() - 1;
            return predecessor.getValues().get(preIndex);
        }
        // The Predecessor in the left subTree
        preIndex = predecessor.getChildren().size() - 1;
        return getPredecessorV(predecessor.getChildren().get(preIndex));
    }

    private K getPredecessorK(IBTreeNode<K, V> predecessor) {
        // Predecessor is the last key
        int preIndex;
        // if it's a leaf, then the predecessor doesn't have children
        if (predecessor.isLeaf()) {
            preIndex = predecessor.getKeys().size() - 1;
            return predecessor.getKeys().get(preIndex);
        }
        // The Predecessor in the left subTree
        preIndex = predecessor.getChildren().size() - 1;
        return getPredecessorK(predecessor.getChildren().get(preIndex));
    }

    private IBTreeNode<K, V> getPredecessor(IBTreeNode<K, V> root, K key) {
        List<K> keys = root.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            if (key.compareTo(keys.get(i)) == 0)
                return root.getChildren().get(i);
        }
        return null;
    }

    private boolean foundKeyInCurrentNode(IBTreeNode<K, V> root, K key) {
        for (K k : root.getKeys()) {
            if (k.compareTo(key) == 0)
                return true;
        }
        return false;
    }

    public void display() {
        this.display(this.root);
    }

    private void display(IBTreeNode<K, V> x) {
        int i;
        for (i = 0; i < x.getNumOfKeys(); ++i) {
            System.out.print(x.getKeys().get(i) + " ");
        }

        if (!x.isLeaf()) {
            for (i = 0; i < x.getNumOfKeys() + 1; ++i)
                this.display(x.getChildren().get(i));
        }

    }

}
