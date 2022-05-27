package ds2.lab5;

import ds2.lab5.b_tree.BTree;
import ds2.lab5.b_tree.IBTree;

public class Main {

    public static void main(String[] args) {
	// write your code here

        IBTree<String, String> ibTree = new BTree<>(1);
        ibTree.search(null);
    }
}
