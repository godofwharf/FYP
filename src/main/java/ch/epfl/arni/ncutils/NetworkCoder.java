package ch.epfl.arni.ncutils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;



public class NetworkCoder {

    // default value for no of blocks in each generation
    public static int no_of_blocks = 50;
    // maximum size of each generation
    public static int segment_threshold = 30000;

    // default value for length of payload
    public static int payloadLen = 1000;

    public static FiniteField ff = new FiniteField(2, 8);

    public static String test_enc = "G:\\FinalYearProject - Copy\\src\\testenc.txt";

    public static byte[] intToByte(int[] input) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(input.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(input);
        byte[] array = byteBuffer.array();
        return array;
    }

    public CodedPacket getCodedRandomCombination(List<CodedPacket> codedPackets, int n) {

        Random r = new Random(System.currentTimeMillis());

        CodedPacket c = new CodedPacket(no_of_blocks, payloadLen, ff);


        for (int j = 0; j < n; j++) {
            int x = r.nextInt(ff.getCardinality());
            CodedPacket copy = codedPackets.get(j).scalarMultiply(x);
            c = c.add(copy);
        }

        return c;
    }

    public CodedPacket getUncodedRandomCombination(List<UncodedPacket> uncodedPackets) {

        CodedPacket[] codewords = new CodedPacket[no_of_blocks];

        for (int i = 0; i < uncodedPackets.size(); i++) {
            codewords[i] = new CodedPacket(uncodedPackets.get(i), no_of_blocks, ff);
        }

        Random r = new Random(System.currentTimeMillis());

        CodedPacket c = new CodedPacket(no_of_blocks, payloadLen, ff);

        for (int j = 0; j < uncodedPackets.size(); j++) {
            int x = r.nextInt(ff.getCardinality());
            CodedPacket copy = codewords[j].scalarMultiply(x);
            c = c.add(copy);
        }

        return c;
    }

    public void printUncodedPackets(String OUTPUT_FILE, List<UncodedPacket> packets, long len, boolean toAppend)
            throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        for (UncodedPacket u : packets) {
            bos.write(u.getPayload());
        }
        byte[] data = bos.toByteArray();

        try {
            FileOutputStream out = new FileOutputStream(new File(OUTPUT_FILE), toAppend);
            out.write(data, 0, (int) len);
            out.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (UncodedPacket p : packets) {
            System.out.println(p);
        }
    }
}
