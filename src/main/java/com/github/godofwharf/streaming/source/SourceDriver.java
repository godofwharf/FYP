package com.github.godofwharf.streaming.source;

import ch.epfl.arni.ncutils.NetworkCoder;
import com.github.godofwharf.streaming.buffer.BufferManager;
import com.github.godofwharf.streaming.graph.Graph;
import com.github.godofwharf.streaming.node.Node;
import com.github.godofwharf.streaming.node.NodeManager;
import com.github.godofwharf.streaming.routing.Router;
import com.github.godofwharf.streaming.singletonfactory.SingletonFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class SourceDriver {

    public final static Logger logger = Logger.getLogger(SourceDriver.class.getName());

    public static double wasted = 0.0;

    public static int count[];

    public static void main(String[] args) throws Exception {

        // Setting file names

        // Make sure the input file is copied into /src/main/resources
        String INPUT_FILE = "/CR7 and cats.mp4";
        String OUTPUT_FILE = "/resdec";

        String NODES_FILE = "/Nodes.txt";
        String EDGES_FILE = "/Edges.txt";

        // Creating graph

        SingletonFactory.setGraph(Graph.read(NODES_FILE, EDGES_FILE));

        Graph g = SingletonFactory.getGraph();

        System.out.println("Graph created successfully");

        // Getting types for nodes

        List<String> types = new ArrayList<String>();

        for (int i = 0; i < g.N; i++) {
            types.add(g.getNode(i).type);
        }

        List<Integer> clients = new ArrayList<Integer>();
        int src = 0;

        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).equals("client"))
                clients.add(i);
            else if (types.get(i).equals("source"))
                src = i;
        }

        // Creating Router

        SingletonFactory.setRouter(new Router(src, clients));
        Router r = SingletonFactory.getRouter();

        System.out.println("Router created successfully");

        File f = new File(SourceDriver.class.getResource(INPUT_FILE).toURI());
        long len = f.length();

        double totalTime = 0.0;

        count = new int[100];

        if (len < NetworkCoder.segment_threshold) {

            // creating network coder
            NetworkCoder nc = new NetworkCoder();
            SingletonFactory.setNetworkCode(nc);

            System.out.println("NetworkCoder created successfully");

            // set up threads for network nodes
            List<Thread> threads = new ArrayList<Thread>();
            List<Node> nodes = new ArrayList<Node>();

            NetworkCoder.no_of_blocks = (int) Math.ceil((double) len / NetworkCoder.payloadLen);

            for (int i = 0; i < g.N; i++) {
                nodes.add(new Node(i, types.get(i)));
                threads.add(new Thread(nodes.get(i)));
            }

            // creating node manager
            NodeManager nm = new NodeManager();
            nm.setNodes(nodes);
            SingletonFactory.setNodeManager(nm);

            System.out.println("NodeManager created successfully");

            // creating buffer manager
            SingletonFactory.setBufferManager(new BufferManager(g.N));
            BufferManager bm = SingletonFactory.getBufferManager();

            System.out.println("BufferManager created successfully");

            // read input file into src buffer
            bm.readIntoBuffer(INPUT_FILE, src);

            long startTime = System.currentTimeMillis();

            // starting all threads
            for (Thread t : threads) {
                t.start();

            }

            // terminating condition
            while (true) {

                int i;
                for (i = 0; i < threads.size(); i++) {
                    if (threads.get(i).getState() != Thread.State.TERMINATED)
                        break;

                }
                if (i == threads.size())
                    break;
            }
            long endTime = System.currentTimeMillis();



            logger.info("Total time for decoding = " + (endTime - startTime) / 1000.0);
            // print decoded packets
            for (int i = 0; i < clients.size(); i++) {
                nc.printUncodedPackets(OUTPUT_FILE + "#" + i + ".mp4",
                        bm.getBufferByNode(clients.get(i)).getUncodedPackets(), len, false);
            }

        } else {
            int i;
            for (i = 0; i * NetworkCoder.segment_threshold < (int) len; i++) {


                // creating network coder
                NetworkCoder nc = new NetworkCoder();
                SingletonFactory.setNetworkCode(nc);

                System.out.println("NetworkCoder created successfully");

                // set up threads for network nodes
                List<Thread> threads = new ArrayList<Thread>();
                List<Node> nodes = new ArrayList<Node>();

                NetworkCoder.no_of_blocks = NetworkCoder.segment_threshold / NetworkCoder.payloadLen;

                for (int j = 0; j < g.N; j++) {
                    nodes.add(new Node(j, types.get(j)));
                    threads.add(new Thread(nodes.get(j)));
                }

                // creating node manager
                NodeManager nm = new NodeManager();
                nm.setNodes(nodes);
                SingletonFactory.setNodeManager(nm);

                System.out.println("NodeManager created successfully");

                // creating buffer manager
                SingletonFactory.setBufferManager(new BufferManager(g.N));
                BufferManager bm = SingletonFactory.getBufferManager();

                System.out.println("BufferManager created successfully");

                byte[] segment = readBytes(f, i * NetworkCoder.segment_threshold);

                bm.readIntoBuffer(segment, src);

                long startTime = System.currentTimeMillis();
                // starting all threads
                for (Thread t : threads) {
                    t.start();
                }

                System.out.print("Threads started");

                // terminating condition
                while (true) {

                    int j;
                    for (j = 0; j < threads.size(); j++) {
                        if (threads.get(j).getState() != Thread.State.TERMINATED)
                            break;

                    }
                    if (j == threads.size())
                        break;
                }
                long endTime = System.currentTimeMillis();
                totalTime += (endTime - startTime);

                logger.info("Decoding time for segment " + i + " = " + (endTime - startTime) / 1000.0);

                System.out.println("All threads terminated");

                if ((i + 1) * NetworkCoder.segment_threshold >= (int) len) {

                    int rem = (int) len % NetworkCoder.segment_threshold;

                    if (rem == 0)
                        rem = NetworkCoder.segment_threshold;

                    // print decoded packets
                    for (int j = 0; j < clients.size(); j++) {
                        nc.printUncodedPackets(OUTPUT_FILE + "#" + j + ".mp4",
                                bm.getBufferByNode(clients.get(j)).getUncodedPackets(), rem, true);
                    }
                } else {
                    // print decoded packets
                    for (int j = 0; j < clients.size(); j++) {
                        nc.printUncodedPackets(OUTPUT_FILE + "#" + j + ".mp4",
                                bm.getBufferByNode(clients.get(j)).getUncodedPackets(), NetworkCoder.segment_threshold,
                                true);
                    }

                }

                r.setCompleted(new ArrayList<Integer>());
            }
            for (int k = 0; k < g.N; k++) {
                System.out.println("Average no. of packets lost due to maxout of Node " + k + " is " + count[k] / i);
            }
            logger.info("Total time for decoding = " + totalTime / 1000.0);
            logger.info("Average time for decoding each segment = " + (totalTime / i) / 1000.0);

            // logger.info("No. of wasted transmissions per segment = " + (wasted/i) );
        }


    }

    public static byte[] readBytes(File f, int skipVal) throws IOException {

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));

        bis.skip(skipVal);

        byte[] file_content = new byte[NetworkCoder.segment_threshold];

        bis.read(file_content, 0, NetworkCoder.segment_threshold);

        bis.close();

        return file_content;
    }

}
