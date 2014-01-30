package ch.epfl.arni.ncutils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ch.epfl.arni.ncutils.CodedPacket;
import ch.epfl.arni.ncutils.FiniteField;
import ch.epfl.arni.ncutils.PacketDecoder;
import ch.epfl.arni.ncutils.UncodedPacket;

public class NCencoding {

    public static void main(String [] args) throws IOException {

        FiniteField ff = FiniteField.getDefaultFiniteField();
        int blockNumber = 10; //no. of blocks
//      int payloadLen=10; //no. of bytes of each block
//   	int payloadLenCoeffs=20;

        File file=new File("D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/testdoc.docx");
        long len=file.length();
        System.out.println(len);
        int payloadLen = (int) (len/blockNumber);
        int payloadLenCoeffs = payloadLen*2;
        int m=0,n=0;
        
        /* create the uncoded packets */
        UncodedPacket[] inputPackets = new UncodedPacket[blockNumber];
        for ( int i = 0 ; i < blockNumber ; i++) 
        {
          byte[] payload = new byte[payloadLen];
          BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File("D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/testdoc.docx")));           
          n = in.read(payload,0,payloadLen); 
          //m=m+payloadLen;
            //Arrays.fill(payload, (byte) ('h'));           
          inputPackets[i] = new UncodedPacket(i, payload);        
        }
        System.out.println(" Input blocks: ");
        printUncodedPackets(Arrays.asList(inputPackets), payloadLen);

        /* prepare the input packets to be sent on the network */
        CodedPacket[] codewords = new CodedPacket[blockNumber];

        for ( int i = 0 ; i < blockNumber ; i++) {
            codewords[i] = new CodedPacket( inputPackets[i], blockNumber, ff);
        }

        System.out.println(" Codewords: ");
        printCodedPackets(Arrays.asList(codewords), payloadLenCoeffs);

        /* create a set of linear combinations that simulate
         * the output of the network
         */

        CodedPacket[] networkOutput = new CodedPacket[blockNumber];

        Random r = new Random(2131231);

        for ( int i = 0 ; i < blockNumber ; i++) {

            networkOutput[i] = new CodedPacket(blockNumber, payloadLen, ff);

            for ( int j = 0 ; j < blockNumber ; j++) {
                int x = r.nextInt(ff.getCardinality());                
                CodedPacket copy = codewords[j].scalarMultiply(x);
                networkOutput[i] = networkOutput[i].add(copy);
                
            }
        }

        
        
        System.out.println(" Network output: ");
        for(int t=0;t<blockNumber;t++)
        {
        	 Vector v = networkOutput[t].payloadVector;
        	 int[] temp=new int[10];
        	 
        	 temp=v.coordinates;
        	// int lenth=temp.length;
        	// System.out.println(lenth);
        	 byte[] data=intToByte(temp);
        	    try {
 			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File("D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/testenc.docx"), true));
 				out.write(data, 0, payloadLen);
 			out.close();
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
        	
        }
       
      //  printCodedPackets(Arrays.asList(networkOutput), payloadLenCoeffs);
        
        /* decode the received packets */
        PacketDecoder decoder = new PacketDecoder(ff, blockNumber, payloadLen);

        System.out.println(" Decoded packets: ");
        for ( int i = 0; i < blockNumber ; i++) {
            List<UncodedPacket> packets = decoder.addPacket(networkOutput[i]);
            
            //Object[] ol = packets.toArray();
         /* UncodedPacket[] pack=packets.toArray(new UncodedPacket[packets.size()]);
            byte[] data=pack[i].payload;          
            try {
     			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File("D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/decres.docx"), true));
     				out.write(data, 0, payloadLen);
     			out.close();
     		} 
            catch (Exception e) 
            {
     			// TODO Auto-generated catch block
     			e.printStackTrace();
     		}
           */ 
            printUncodedPackets(packets, payloadLen);
        }
       
       
    }

   
    private static void printUncodedPackets(Iterable<UncodedPacket> packets, int payloadLen) {
    	
    	for (UncodedPacket p : packets) {            
            System.out.println(p);
        }
    }

  
    
    private static void printCodedPackets(Iterable<CodedPacket> packets, int payloadLen)
    {

  	
        for (CodedPacket p : packets) 
        {
            System.out.println(p);    
        }
    }
    
    public static byte[] intToByte(int[] input)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(input.length*4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(input);
        byte[] array = byteBuffer.array();
        return array;
    }
  
    //return ByteBuffer.allocate(4).putInt(yourInt).array();
    
}

