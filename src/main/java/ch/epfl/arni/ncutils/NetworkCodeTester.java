package ch.epfl.arni.ncutils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;


/**
 * TODO
 * Replace sort function with linear time solution.
 * Work with various values for block number and observe results
 */
public class NetworkCodeTester {

    private static FiniteField ff = new FiniteField(2, 8);
    //no. of blocks
    private static int BLOCK_NUMBER = 50;
    private static int sz_threshold = 1000;
    private static String INPUT_FILE = "D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/testdoc.txt";
    private static String TEST_ENC = "D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/testenc.txt";
    private static String OUTPUT_FILE = "D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/resdec.txt";
    private static boolean toAppend = false;

    public static void main(String[] args) throws IOException {

        //	int payloadLen=10; //no. of bytes of each block
        //	int payloadLenCoeffs=20;

        // File file=new File("D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/testdoc.txt");

        File file = new File(INPUT_FILE);
        long len = file.length();

        System.out.println(len);

        int payloadLen, numNonZero, payloadLenCoeffs;

        /* create the uncoded packets */

        UncodedPacket[] inputPackets = null;

        if (len > sz_threshold) {
            long numBytes = 0;
            toAppend = true;

            payloadLen = (int) Math.ceil((double) sz_threshold / BLOCK_NUMBER);
            System.out.println(payloadLen);

            payloadLenCoeffs = payloadLen * 2;

            numNonZero = (int) (sz_threshold / payloadLen);

            if (sz_threshold % payloadLen > 0)
                numNonZero += 1;

            BLOCK_NUMBER = numNonZero;
            int k = 1;
            while (numBytes < len) {
                if (numBytes + sz_threshold > len) {
                    payloadLen = (int) (len - numBytes) / 200;
                    payloadLenCoeffs = payloadLen * 2;

                    numNonZero = (int) ((len - numBytes) / payloadLen);

                    if ((len - numBytes) % payloadLen > 0)
                        numNonZero += 1;

                    BLOCK_NUMBER = numNonZero;

                }
                k++;

                UncodedPacket[] temp = createUncodedPackets(payloadLen, sz_threshold * k, numBytes);
                /* prepare the input packets to be sent on the network */
                CodedPacket[] networkOutput = codePackets(temp, payloadLen, payloadLenCoeffs);
                /* decode the received packets */
                decodeCodedPackets(networkOutput, payloadLen);
                numBytes += sz_threshold;

                //if(k == 3) break;
            }

        } else {
            payloadLen = (int) Math.ceil((double) len / BLOCK_NUMBER);
            numNonZero = (int) (len / payloadLen);

            if (len % payloadLen > 0)
                numNonZero += 1;

            BLOCK_NUMBER = numNonZero;

            System.out.println(payloadLen);
            payloadLenCoeffs = payloadLen * 2;

            try {
                inputPackets = createUncodedPackets(payloadLen, len, 0);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(" Input blocks: ");
            // printUncodedPackets(Arrays.asList(inputPackets), payloadLen);
            /* prepare the input packets to be sent on the network */

            CodedPacket[] networkOutput = codePackets(inputPackets, payloadLen, payloadLenCoeffs);

            // printCodedPackets(Arrays.asList(networkOutput), payloadLenCoeffs);
            /* decode the received packets */
            decodeCodedPackets(networkOutput, payloadLen);
            System.out.println(" Input blocks: ");
            // printUncodedPackets(Arrays.asList(inputPackets), payloadLen);
        }



    }

    private static CodedPacket[] codePackets(UncodedPacket[] inputPackets, int payloadLen, int payloadLenCoeffs) {
        CodedPacket[] codewords = new CodedPacket[BLOCK_NUMBER];

        for (int i = 0; i < BLOCK_NUMBER; i++) {
            codewords[i] = new CodedPacket(inputPackets[i], BLOCK_NUMBER, ff);
        }

        System.out.println(" Codewords: ");
        //printCodedPackets(Arrays.asList(codewords), payloadLenCoeffs);

        /* create a set of linear combinations that simulate
         * the output of the network
         */

        CodedPacket[] networkOutput = new CodedPacket[BLOCK_NUMBER];

        Random r = new Random(2131231);

        for (int i = 0; i < BLOCK_NUMBER; i++) {

            networkOutput[i] = new CodedPacket(BLOCK_NUMBER, payloadLen, ff);

            for (int j = 0; j < BLOCK_NUMBER; j++) {
                int x = r.nextInt(ff.getCardinality());
                CodedPacket copy = codewords[j].scalarMultiply(x);
                networkOutput[i] = networkOutput[i].add(copy);

            }
        }

        System.out.println(" Network output: ");
        for (int t = 0; t < BLOCK_NUMBER; t++) {
            Vector v = networkOutput[t].payloadVector;
            int[] temp = new int[10];

            temp = v.coordinates;
            byte[] data = intToByte(temp);

            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(TEST_ENC), true));
                out.write(data, 0, payloadLen);
                out.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return networkOutput;
    }

    private static void decodeCodedPackets(CodedPacket[] networkOutput, int payloadLen) {
        PacketDecoder decoder = new PacketDecoder(ff, BLOCK_NUMBER, payloadLen);
        System.out.println(" Decoded packets: ");
        List<UncodedPacket> packets = null;
        List<UncodedPacket> output = new ArrayList<UncodedPacket>();

        boolean[] seen = new boolean[BLOCK_NUMBER];
        Arrays.fill(seen, false);

        int cnt = 0;


        for (int i = 0; i < BLOCK_NUMBER; i++) {

            packets = decoder.addPacket(networkOutput[i]);

            for (UncodedPacket p : packets) {

                if (!seen[p.getId()]) {

                    output.add(p);
                    System.out.println(output);
                    seen[p.getId()] = true;
                    cnt += 1;

                }

            }

            if (cnt == BLOCK_NUMBER)
                System.out.println("No. of coded packets required for decoding: " + (i + 1));


        }

        NetworkCodeTester nc = new NetworkCodeTester();
        Collections.sort(output, nc.new PacketComparator());

        try {

            printUncodedPackets(output, payloadLen);
        } catch (IOException e) {
            System.out.println("Error encountered in printing uncoded packets");
            e.printStackTrace();
        }


    }

    private static void printUncodedPackets(List<UncodedPacket> packets, int payloadLen) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        for (UncodedPacket u : packets) {
            bos.write(u.getPayload());
        }
        byte[] data = bos.toByteArray();

        try {
            FileOutputStream out = new FileOutputStream(new File(OUTPUT_FILE), toAppend);
            out.write(data, 0, data.length);
            out.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (UncodedPacket p : packets) {
            System.out.println(p);
        }
    }

    private static void printCodedPackets(Iterable<CodedPacket> packets, int payloadLen) {

        for (CodedPacket p : packets) {
            System.out.println(p);
        }
    }

    public static byte[] intToByte(int[] input) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(input.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(input);
        byte[] array = byteBuffer.array();
        return array;
    }

    public static UncodedPacket[] createUncodedPackets(int payloadLen, long len, long initSkipVal) throws IOException {
        long skipVal = initSkipVal;
        int n = 0;
        UncodedPacket[] inputPackets = new UncodedPacket[BLOCK_NUMBER];


        for (int i = 0; i < BLOCK_NUMBER; i++) {

            byte[] payload = new byte[payloadLen];

            BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(INPUT_FILE)));

            if (skipVal < len) {
                in.skip(skipVal);
                n = in.read(payload, 0, payloadLen);
                skipVal += n;
            }
            inputPackets[i] = new UncodedPacket(i, payload);
            byte b[] = inputPackets[i].getPayload();

            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("Error in closing input stream");
                e.printStackTrace();
            }
            //debug info
            System.out.print("uncoded packet " + i + ":");
            for (byte c : b) {
                System.out.print(" " + c);
            }
            System.out.println();
        }
        return inputPackets;
    }

    public void encode(String inFile) {

        INPUT_FILE = inFile;



    }


    public class PacketComparator implements Comparator<UncodedPacket> {
        @Override public int compare(UncodedPacket a, UncodedPacket b) {
            return a.getId() - b.getId();
        }

    }
}

