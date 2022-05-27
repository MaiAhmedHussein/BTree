package ds2.lab5.b_tree;

import java.util.List;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {

    private final int t;
    private IBTreeNode<K, V> root;

    public BTree(int t) {
        this.t = t;
        this.root = null;
    }

    @Override
    public int getMinimumDegree() {
        return t;
    }

    @Override
    public IBTreeNode<K, V> getRoot() {
        return null;
    }

    @Override
    public void insert(K key, V value) {

    }

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
        if (subtree != null && subtree.getNumOfKeys() < t) {
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

    private void moveKeys(IBTreeNode<K,V> root, IBTreeNode<K,V> xc, IBTreeNode<K,V> sibling) {
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
                if (i > 0 && xChildren.get(i - 1).getNumOfKeys() >= t) {
                    return xChildren.get(i - 1);
                }

                // Check right sibling with at least t keys
                if (i < xChildren.size() - 1 && xChildren.get(i + 1).getNumOfKeys() >= t) {
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
        if (predecessor != null && predecessor.getNumOfKeys() >= t) {
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
        if (successor != null && successor.getNumOfKeys() >= t) {
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
}