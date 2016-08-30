package com.github.godofwharf.streaming.routing;

import Tree.GenericTree;
import Tree.GenericTreeNode;

import java.util.HashMap;

public class Treebone {

    private HashMap<Integer, GenericTreeNode<Integer>> treeHash;

    private GenericTree<Integer> gT;

    public Treebone(int src) {
        treeHash = new HashMap<Integer, GenericTreeNode<Integer>>();

        gT = new GenericTree<Integer>();

        gT.setRoot(new GenericTreeNode<Integer>(src));
    }

    public void addToTreebone(int id) {

        GenericTreeNode<Integer> a = new GenericTreeNode<Integer>(id);

        GenericTreeNode<Integer> r = gT.bfs();

        r.addChild(a);


    }

    public void insert(int id, GenericTreeNode<Integer> n) {
        treeHash.put(id, n);
    }

    public void delete(int id) {
        treeHash.remove(id);
    }

    public GenericTreeNode<Integer> lookup(int id) {

        return treeHash.get(id);

    }

    public double EST(int t, int L, double k) {
        // int aT = getAgeThreshold(t);
        int aT = 5;
        return aT / (k - 1) * (1 - Math.pow((aT) / (L - t), k - 1));
    }

}
