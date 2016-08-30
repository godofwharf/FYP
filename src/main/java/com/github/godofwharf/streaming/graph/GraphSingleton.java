package com.github.godofwharf.streaming.graph;

public class GraphSingleton {

    private static Graph ref;

    public static Graph getGraph() {
        if (ref == null) {
            System.out.println("Error - graph not created yet");
            System.exit(0);
        }
        return ref;
    }

    public static void setGraph(Graph g) {
        ref = g;
    }

}
