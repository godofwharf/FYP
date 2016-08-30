package com.github.godofwharf.streaming.singletonfactory;

import ch.epfl.arni.ncutils.NetworkCoder;
import com.github.godofwharf.streaming.buffer.BufferManager;
import com.github.godofwharf.streaming.graph.Graph;
import com.github.godofwharf.streaming.node.NodeManager;
import com.github.godofwharf.streaming.routing.Router;

public class SingletonFactory {
    private static BufferManager bm;
    private static Graph g;
    private static Router r;
    private static NodeManager nm;
    private static NetworkCoder nc;

    public static NetworkCoder getNetworkCode() {
        if (nc == null) {
            System.out.println("Error: Network code not initialized");
            System.exit(0);
        }
        return nc;
    }

    public static void setNetworkCode(NetworkCoder n) {
        nc = n;
    }

    public static BufferManager getBufferManager() {
        if (bm == null) {
            System.out.println("Error: Buffer manager not initialized");
            System.exit(0);
        }
        return bm;
    }

    public static void setBufferManager(BufferManager b) {
        bm = b;
    }

    public static Router getRouter() {
        if (r == null) {
            System.out.println("Error - router not initialized");
            System.exit(0);
        }
        return r;
    }

    public static void setRouter(Router router) {
        r = router;
    }

    public static Graph getGraph() {
        if (g == null) {
            System.out.println("Error - graph not created yet");
            System.exit(0);
        }
        return g;

    }

    public static void setGraph(Graph graph) {
        g = graph;
    }

    public static NodeManager getNodeManager() {
        if (nm == null) {
            System.out.println("Error - node manager not created yet");
            System.exit(0);
        }
        return nm;

    }

    public static void setNodeManager(NodeManager n) {
        nm = n;
    }


}
