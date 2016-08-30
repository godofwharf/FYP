package com.github.godofwharf.streaming.buffer;


import ch.epfl.arni.ncutils.NetworkCoder;
import ch.epfl.arni.ncutils.UncodedPacket;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BufferManager {

    private static byte[] byte_array = null;
    private List<Buffer> buffers;

    public BufferManager(int no_of_nodes) {
        buffers = new ArrayList<Buffer>();

        for (int i = 0; i < no_of_nodes; i++) {
            buffers.add(new Buffer(i, null, null));
        }
        byte_array = new byte[5000];
    }

    public static byte[][] divideArray(byte[] source, int chunksize) {
        byte[][] ret = new byte[(int) Math.ceil(source.length / (double) chunksize)][chunksize];

        int start = 0;

        for (int i = 0; i < ret.length; i++) {
            ret[i] = Arrays.copyOfRange(source, start, start + chunksize);
            start += chunksize;
        }

        return ret;
    }

    public static List<UncodedPacket> createUncodedPackets(byte[][] b) {
        List<UncodedPacket> uPackets = new ArrayList<UncodedPacket>();

        int id = 0;
        for (byte[] payload : b) {
            UncodedPacket u = new UncodedPacket(id, payload);
            uPackets.add(u);
            id++;
        }

        return uPackets;
    }

    public void readIntoBuffer(byte[] segment, int id) {

        // split byte_array into chunks of length payloadLen
        byte[][] packets = divideArray(segment, NetworkCoder.payloadLen);

        List<UncodedPacket> uPackets = createUncodedPackets(packets);

        for (UncodedPacket u : uPackets)
            System.out.println(u);

        getBufferByNode(id).setUncodedPackets(uPackets);
    }

    public void readIntoBuffer(String input_file, int id) throws IOException, URISyntaxException {
        File f = new File(BufferManager.class.getResource(input_file).toURI());
        long len = f.length();

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));

        // setting no_of_blocks
        bis.read(byte_array, 0, (int) len);

        int rem = NetworkCoder.no_of_blocks * NetworkCoder.payloadLen - (int) len;

        byte[] file_content = Arrays.copyOf(byte_array, (int) len + rem);

        // printing the read bytes
        for (int i = 0; i < len + rem; i++)
            System.out.println("Byte #" + i + ":" + file_content[i]);

        // split byte_array into chunks of length payloadLen
        byte[][] packets = divideArray(file_content, NetworkCoder.payloadLen);

        List<UncodedPacket> uPackets = createUncodedPackets(packets);

		/*for(UncodedPacket u: uPackets)
            System.out.println(u);
		*/

        getBufferByNode(id).setUncodedPackets(uPackets);


    }

    public Buffer getBufferByNode(int node_num) {
        return buffers.get(node_num);
    }



    public void setBufferByNode(int node_num, Buffer b) {

        buffers.set(node_num, b);
    }

    public List<Buffer> getBuffers() {
        return buffers;
    }

    public void setBuffers(List<Buffer> b) {
        buffers = b;
    }

    public void transmit(int src, int des) {
        Buffer b1 = getBufferByNode(src);
        Buffer b2 = getBufferByNode(des);


        b2.setUncodedPackets(null);
        b2.setCodedPackets(b1.getCodedPackets());
    }

}
