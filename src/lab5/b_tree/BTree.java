package lab5.b_tree;

import javax.management.RuntimeErrorException;
import java.util.LinkedList;
import java.util.List;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {

    private final int minimumDegree;
    private IBTreeNode<K, V> root;
    private final int max_keys_added;

    public BTree(int minimumDegree) {
        if (minimumDegree < 2) {
            throw new RuntimeErrorException(new Error());
        }
        this.minimumDegree = minimumDegree;
        this.max_keys_added = minimumDegree * 2 - 1;
        //max number of keys allowed in one node
    }

    @Override
    public int getMinimumDegree() {
        return this.minimumDegree;
    }

    @Override
    public IBTreeNode<K, V> getRoot() {
        return root;
    }

    @Override
    public void insert(K key, V value) {
        if (key == null || value == null || search(key) != null) { //invalid input
            return;
        }

        if (this.root == null) { //tree is empty
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
        // to get the position where to insert the new key
        IBTreeNode<K, V> insertInto = getInsertionPosition(this.root, key);
        List<K> keys = insertInto.getKeys();
        //if the inserted value is greater than the key in the list
        //then find the right to place to insert into inside the node
        int i;
        for (i = 0; i < insertInto.getNumOfKeys(); i++) {
            //find the right to place to insert into inside the node
            if (key.compareTo(keys.get(i)) < 0) {
                break;
            }
        }
        keys.add(i, key);
        insertInto.getValues().add(i, value);
        insertInto.setNumOfKeys(keys.size());
    }

    @Override
    public V search(K key) {
        if (key == null) { //invalid input
            throw new RuntimeErrorException(new Error());
        }
        IBTreeNode<K, V> found = search(this.root, key);

        if (found == null) { //not found
            return null;
        }

        if (found.getKeys().contains(key)) {
            return found.getValues().get(found.getKeys().indexOf(key));
        }

        return null;
    }

    @Override
    public boolean delete(K key) {
        if (search(key) == null) return false; // doesn't exist

        delete(this.root, key);

        // if the root has no keys
        if (this.root.getKeys().isEmpty() && !this.root.isLeaf())
            this.root = this.root.getChildren().get(0);

        return true;
    }

    private IBTreeNode<K, V> search(IBTreeNode<K, V> node, K key) {
        while (node != null && !node.isLeaf()) {
            List<K> keys = node.getKeys();
            for (int i = 0; i < keys.size(); i++) {
                K k = keys.get(i);
                if (key.compareTo(k) == 0) { // key found
                    return node;
                } else if (key.compareTo(k) < 0) {
                    // search in the left branch
                    List<IBTreeNode<K, V>> children = node.getChildren();
                    node = children.get(i);
                    break;
                } else if (i == keys.size() - 1) {
                    // search in the right branch
                    List<IBTreeNode<K, V>> children = node.getChildren();
                    node = children.get(i + 1);
                    break;
                }
            }
        }

        if (node != null && node.isLeaf() && isKeyInCurrentNode(node, key)) {
            return node;
        } else {
            return null;
        }
    }

    private IBTreeNode<K, V> getInsertionPosition(IBTreeNode<K, V> node, K key) {
        // If root is full, then tree grows in height,
        // so we need to split it
        if (node.getNumOfKeys() == max_keys_added) {
            splitRoot(node);
            node = this.root;
        }
        while (!node.isLeaf()) {
            List<K> keys = node.getKeys();
            for (int i = 0; i < keys.size(); i++) {
                K k = keys.get(i);
                if (key.compareTo(k) < 0) {
                    // as the key inserted smaller than the key in the list
                    // traverse through left child
                    List<IBTreeNode<K, V>> children = node.getChildren();
                    IBTreeNode<K, V> child = children.get(i);
                    if (child.getNumOfKeys() == max_keys_added) {
                        child = split(node, child, key);
                    }
                    node = child;
                    break;
                } else if (i == keys.size() - 1) {
                    //traverse through the most right child
                    List<IBTreeNode<K, V>> children = node.getChildren();
                    IBTreeNode<K, V> child = children.get(i + 1);
                    if (child.getNumOfKeys() == max_keys_added) {
                        child = split(node, child, key);
                    }
                    node = child;
                    break;
                }
            }
        }
        return node;
    }


    private void splitRoot(IBTreeNode<K, V> node) {
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
        int i;
        for (i = 0; i < node.getNumOfKeys() / 2; i++) {
            //left side of the split
            newKeys.add(keys.get(i));
            newValues.add(values.get(i));
            if (!node.isLeaf()) {
                newChildren.add(children.get(i));
            }
        }
        if (!node.isLeaf()) {
            //assign the new leaves
            newChildren.add(children.get(i));
        }
        leftSplit.setKeys(newKeys);
        leftSplit.setValues(newValues);
        leftSplit.setChildren(newChildren);
        leftSplit.setNumOfKeys(newKeys.size());

        newKeys = new LinkedList<>();
        newValues = new LinkedList<>();
        newChildren = new LinkedList<>();

        //the middle item of the split
        newKeys.add(keys.get(i));
        newValues.add(values.get(i));
        newChildren.add(leftSplit);
        newChildren.add(rightSplit);
        newRoot.setKeys(newKeys);
        newRoot.setValues(newValues);
        newRoot.setChildren(newChildren);
        newRoot.setNumOfKeys(newKeys.size());

        newKeys = new LinkedList<>();
        newValues = new LinkedList<>();
        newChildren = new LinkedList<>();

        i++;
        for (; i < node.getNumOfKeys(); i++) {
            //the right side of the split
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
        this.root = newRoot;
    }

    // where node is the parent of the child
    private IBTreeNode<K, V> split(IBTreeNode<K, V> node, IBTreeNode<K, V> child, K key) {
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
        int i;
        for (i = 0; i < child.getNumOfKeys() / 2; i++) {
            //left side of the split
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

        //middle item of the split
        newKeys = node.getKeys();
        newValues = node.getValues();
        newChildren = node.getChildren();
        int j;
        K midKey = keys.get(i);
        for (j = 0; j < newKeys.size(); j++) {
            if (keys.get(i).compareTo(newKeys.get(j)) < 0) {
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

        newKeys = new LinkedList<>();
        newValues = new LinkedList<>();
        newChildren = new LinkedList<>();

        i++;
        for (; i < child.getNumOfKeys(); i++) {
            //right side of the split
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

        if (key.compareTo(midKey) > 0) {
            return rightSplit;
        }
        return leftSplit;
    }

    private void delete(IBTreeNode<K, V> x, K key) {

        // Three cases for deletion
        // CASE I: Key is in node x, and x is a leaf
        if (x.isLeaf()) {
            List<K> xKeys = x.getKeys();
            List<V> xValues = x.getValues();

            int index = xKeys.indexOf(key);
            xKeys.remove(key);
            xValues.remove(index);

            x.setKeys(xKeys);
            x.setValues(xValues);
            return;
        }

        // CASE II: Key is in the node x, and x is an internal node
        // Three subcases
        if (isKeyInCurrentNode(x, key)) {
            internalX(x, key);
            return;
        }

        // CASE III: Key isn't in internal node x, but in child x.ci
        internalXExternalK(x, key);
    }

    // For CASE II Deletion
    private void internalX(IBTreeNode<K, V> x, K key) {
        // Three subcases for CASE II

        // index of value which equivalent to key
        int index = x.getKeys().indexOf(key);   // index of value which equivalent to key

        // CASE 2a:
        // child y that precedes k in node x has at least t keys, then
        // find the predecessor k` of k in the subtree rooted at y
        IBTreeNode<K, V> predecessor = getPredChild(x, key);

        if (predecessor != null && predecessor.getNumOfKeys() >= minimumDegree) {
            K predKey = getPredKey(predecessor);
            V predValue = getPredValue(predecessor);

            // delete the predecessor key
            delete(predecessor, predKey);
            List<K> xKeys = x.getKeys();
            List<V> xValues = x.getValues();
            xKeys.set(index, predKey);
            xValues.set(index, predValue);
            x.setKeys(xKeys);
            x.setValues(xValues);
            return;
        }

        // CASE 2b:
        // child y has fewer than t keys, then symmetrically examine
        // the child z that follow k in node x, if z has at least t keys, then
        // find the successor k` of k in the subtree rooted at z
        IBTreeNode<K, V> successor = getSuccessorChild(x, key);
        if (successor != null && successor.getNumOfKeys() >= minimumDegree) {
            K successorKey = getSuccessorKey(successor);
            V successorValue = getSuccessorValue(successor);

            // delete the successor key
            delete(successor, successorKey);
            List<K> xKeys = x.getKeys();
            List<V> xValues = x.getValues();
            xKeys.set(index, successorKey);
            xValues.set(index, successorValue);
            x.setKeys(xKeys);
            x.setValues(xValues);
            return;
        }

        // CASE 2c:
        // when both y, z have only (t-1) keys, merge k and all of z into y
        // x loses both k and the pointer to z, and now y contains (2t-1) keys
        // free z and recursively delete k from y
        assert predecessor != null;
        assert successor != null;
        merge(x, key, predecessor, successor);
        delete(predecessor, key);
    }

    // CASE III: DELETION
    private void internalXExternalK(IBTreeNode<K, V> x, K key) {

        // if x.ci has only t-1 keys, execute 3a or 3b
        IBTreeNode<K, V> subtree = getSubtree(x, key);

        if (subtree != null && subtree.getNumOfKeys() < minimumDegree) {
            IBTreeNode<K, V> sibling = getImmediateSibling(x, subtree);

            if (sibling != null) {
                // CASE 3a:
                // when x.ci has immediate sibling with at least t keys
                // give x.ci an extra key by moving a key from x down to x.ci
                // move a key from x.ci immediate left or right sibling up to x
                moveKeys(x, subtree, sibling);
                delete(subtree, key);
            } else {

                // CASE 3b:
                // when x.ci and both of x.ci immediate siblings have (t-1) keys
                // merge x.ci with one of its siblings which involves moving
                // a key from x down into the new merged node to become the median key for this node
                int index = getSiblingIndex(x, subtree);
                List<IBTreeNode<K, V>> xChildren = x.getChildren();

                boolean isRightSibling = index > 0 && xChildren.get(index - 1) == subtree;
                sibling = xChildren.get(index);

                K medianKey;
                if (isRightSibling) {
                    medianKey = x.getKeys().get(index - 1);
                    merge(x, medianKey, subtree, sibling);
                    delete(subtree, key);
                } else {
                    medianKey = x.getKeys().get(index);
                    merge(x, medianKey, sibling, subtree);
                    delete(sibling, key);
                }
            }
        } else {
            assert subtree != null;
            delete(subtree, key);
        }
    }

    private boolean isKeyInCurrentNode(IBTreeNode<K, V> x, K key) {
        List<K> keys = x.getKeys();
        for (K k : keys) {
            if (key.compareTo(k) == 0)
                return true;
        }
        return false;
    }

    private IBTreeNode<K, V> getPredChild(IBTreeNode<K, V> x, K key) {
        List<K> keys = x.getKeys();
        for (int i = 0; i < keys.size(); ++i) {
            if (key.compareTo(keys.get(i)) == 0)
                return x.getChildren().get(i);
        }
        return null;
    }

    private K getPredKey(IBTreeNode<K, V> predecessor) {
        int lastIndex;
        if (predecessor.isLeaf()) {
            lastIndex = predecessor.getKeys().size() - 1;
            return predecessor.getKeys().get(lastIndex);
        }
        lastIndex = predecessor.getChildren().size() - 1;
        return getPredKey(predecessor.getChildren().get(lastIndex));
    }

    private V getPredValue(IBTreeNode<K, V> predecessor) {
        int lastIndex;
        if (predecessor.isLeaf()) {
            lastIndex = predecessor.getValues().size() - 1;
            return predecessor.getValues().get(lastIndex - 1);
        }
        lastIndex = predecessor.getChildren().size() - 1;
        return getPredValue(predecessor.getChildren().get(lastIndex));
    }

    private IBTreeNode<K, V> getSuccessorChild(IBTreeNode<K, V> x, K key) {
        List<K> keys = x.getKeys();
        for (int i = 0; i < keys.size(); ++i) {
            if (key.compareTo(keys.get(i)) == 0)
                return x.getChildren().get(i + 1);
        }
        return null;
    }

    private K getSuccessorKey(IBTreeNode<K, V> successor) {
        int firstIndex = 0;
        if (successor.isLeaf()) {
            return successor.getKeys().get(firstIndex);
        }

        return getSuccessorKey(successor.getChildren().get(firstIndex));
    }

    private V getSuccessorValue(IBTreeNode<K, V> successor) {
        int firstIndex = 0;
        if (successor.isLeaf()) {
            return successor.getValues().get(firstIndex);
        }

        return getSuccessorValue(successor.getChildren().get(firstIndex));
    }

    private void merge(IBTreeNode<K, V> x, K key, IBTreeNode<K, V> y, IBTreeNode<K, V> z) {
        int index;

        // point to the value of key in values list
        List<K> yKeys = y.getKeys();
        index = x.getKeys().indexOf(key);
        List<V> yValues = y.getValues();
        yKeys.add(key);
        yValues.add(x.getValues().get(index));
        yKeys.addAll(z.getKeys());
        yValues.addAll(z.getValues());

        // set y keys, and values
        y.setKeys(yKeys);
        y.setValues(yValues);

        // add the children pointers if the nodes are internal nodes
        if (!y.isLeaf()) {
            List<IBTreeNode<K, V>> zChildren = z.getChildren();
            List<IBTreeNode<K, V>> yChildren = y.getChildren();
            yChildren.addAll(zChildren);
            y.setChildren(yChildren);
        }

        // after that, remove the key from the parent
        List<K> keysOfX = x.getKeys();
        List<V> valuesOfX = x.getValues();
        index = keysOfX.indexOf(key);
        keysOfX.remove(key);
        valuesOfX.remove(index);
        x.setKeys(keysOfX);
        x.setValues(valuesOfX);

        // finally, remove the pointer to the z node from parent
        List<IBTreeNode<K, V>> childrenOfX = x.getChildren();
        childrenOfX.remove(childrenOfX.get(index + 1));
        x.setChildren(childrenOfX);
    }

    private IBTreeNode<K, V> getSubtree(IBTreeNode<K, V> x, K key) {
        List<K> keys = x.getKeys();
        for (int i = 0; i < keys.size(); ++i) {
            K k = keys.get(i);

            if (key.compareTo(k) <= 0) {
                return x.getChildren().get(i);
            }
            if (i == keys.size() - 1) {
                return x.getChildren().get(i + 1);
            }
        }
        return null;
    }

    private IBTreeNode<K, V> getImmediateSibling(IBTreeNode<K, V> x, IBTreeNode<K, V> subtree) {
        List<IBTreeNode<K, V>> xChildren = x.getChildren();
        for (int i = 0; i < xChildren.size(); ++i) {
            if (xChildren.get(i) == subtree) {
                // left sibling with additional keys
                if (i > 0 && xChildren.get(i - 1).getNumOfKeys() >= minimumDegree) {
                    return xChildren.get(i - 1);
                }
                // right sibling with additional keys
                else if (i != xChildren.size() - 1 && xChildren.get(i + 1).getNumOfKeys() >= minimumDegree) {
                    return xChildren.get(i + 1);
                }
            }
        }
        return null;
    }

    void moveKeys(IBTreeNode<K, V> x, IBTreeNode<K, V> xc, IBTreeNode<K, V> sibling) {

        int index = 0;
        boolean rightSibling = false;

        List<IBTreeNode<K, V>> xChildren = x.getChildren();
        for (int i = 0; i < xChildren.size(); ++i) {
            if (xChildren.get(i) == xc) {
                index = i;
                rightSibling = i != xChildren.size() - 1 && xChildren.get(i + 1) == sibling;
                break;
            }
        }

        if (rightSibling) {
            // move key and value from parent to xc
            List<K> xKeys = x.getKeys();
            List<V> xValues = x.getValues();
            K xKeyToBeMovedDown = xKeys.get(index);
            V xValueToBeMovedDown = xValues.get(index);
            List<K> subtreeKeys = xc.getKeys();
            List<V> subtreeValues = xc.getValues();
            subtreeKeys.add(xKeyToBeMovedDown);
            subtreeValues.add(xValueToBeMovedDown);
            xc.setKeys(subtreeKeys);
            xc.setValues(subtreeValues);

            // move value from left sibling to the x
            List<K> siblingKeys = sibling.getKeys();
            List<V> siblingValues = sibling.getValues();
            K siblingKeyToBeMovedUp = siblingKeys.remove(0);
            V siblingValueToBeMovedUp = siblingValues.remove(0);
            xKeys.set(index, siblingKeyToBeMovedUp);
            xValues.set(index, siblingValueToBeMovedUp);
            x.setKeys(xKeys);
            x.setValues(xValues);
            sibling.setKeys(siblingKeys);
            sibling.setValues(siblingValues);

            // move pointers of children if not root from sibling to x.ci
            if (!xc.isLeaf()) {
                List<IBTreeNode<K, V>> siblingChildren = sibling.getChildren();
                List<IBTreeNode<K, V>> subtreeChildren = xc.getChildren();
                subtreeChildren.add(siblingChildren.remove(0));
                sibling.setChildren(siblingChildren);
                xc.setChildren(subtreeChildren);
            }

        } else {  // left Sibling __ The same steps as right

            index -= 1;

            // move key and value from parent to xc
            List<K> xKeys = x.getKeys();
            List<V> xValues = x.getValues();
            K xKeyToBeMovedDown = xKeys.get(index);
            V xValueToBeMovedDown = xValues.get(index);
            List<K> subtreeKeys = xc.getKeys();
            List<V> subtreeValues = xc.getValues();
            subtreeKeys.add(0, xKeyToBeMovedDown);
            subtreeValues.add(0, xValueToBeMovedDown);
            xc.setKeys(subtreeKeys);
            xc.setValues(subtreeValues);

            // move value from right sibling to the x
            List<K> siblingKeys = sibling.getKeys();
            List<V> siblingValues = sibling.getValues();
            // right sibling first index to be moves
            K siblingKeyToBeMovedUp = siblingKeys.remove(siblingKeys.size() - 1);
            V siblingValueToBeMovedUp = siblingValues.remove(siblingValues.size() - 1);
            xKeys.set(index, siblingKeyToBeMovedUp);
            xValues.set(index, siblingValueToBeMovedUp);
            x.setKeys(xKeys);
            x.setValues(xValues);
            sibling.setKeys(siblingKeys);
            sibling.setValues(siblingValues);

            // move pointers of children if not root from sibling to x.ci
            if (!xc.isLeaf()) {
                List<IBTreeNode<K, V>> siblingChildren = sibling.getChildren();
                List<IBTreeNode<K, V>> subtreeChildren = xc.getChildren();
                subtreeChildren.add(0, siblingChildren.remove(siblingChildren.size() - 1));
                sibling.setChildren(siblingChildren);
                xc.setChildren(subtreeChildren);
            }
        }
    }

    private int getSiblingIndex(IBTreeNode<K, V> x, IBTreeNode<K, V> subtree) {
        List<IBTreeNode<K, V>> xChildren = x.getChildren();
        for (int i = 0; i < xChildren.size(); ++i) {
            if (xChildren.get(i) == subtree) {
                if (i != xChildren.size() - 1) return i + 1;
                else return i - 1;
            }
        }
        return 0;
    }
}
