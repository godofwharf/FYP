package ch.epfl.arni.ncutils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ch.epfl.arni.ncutils.CodedPacket;
import ch.epfl.arni.ncutils.FiniteField;
import ch.epfl.arni.ncutils.PacketDecoder;
import ch.epfl.arni.ncutils.UncodedPacket;

public class NCencoding 
{

    public static void main(String [] args) throws IOException {

        FiniteField ff = new FiniteField(2,8);
        int blockNumber = 100; //no. of blocks
    //	int payloadLen=10; //no. of bytes of each block
    //	int payloadLenCoeffs=20;

        File file=new File("D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/testdoc.txt");
        long len=file.length();
        System.out.println(len);
     
        int payloadLen = (int) Math.ceil((double) len/blockNumber);
        System.out.println(payloadLen);
       int payloadLenCoeffs = payloadLen*4;
        int n=0,skipval=0;
        
        String msg = "hello how are you?";    
        
        /* create the uncoded packets */
        UncodedPacket[] inputPackets = new UncodedPacket[blockNumber];
        

        	
        for ( int i = 0 ; i < blockNumber; i++) 
        {
       
          byte[] payload = new byte[payloadLen];
          
          BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File("D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/testdoc.txt")));           

          if(skipval < len) {
        	  in.skip(skipval);
        	  n = in.read(payload,0,payloadLen);
        	  skipval += n;
          }
         // Arrays.fill(payload, (byte) ('h'));   
          //inputPackets[i] = new UncodedPacket(i, msg[i]);
         
          inputPackets[i] = new UncodedPacket(i, payload);
          byte b[] = inputPackets[i].getPayload();
          
          //debug info
          System.out.print("uncoded packet "+ i + ":");
          for(byte c:b) {
        	  System.out.print(" " + c);
          }
          System.out.println();
        }
        System.out.println(" Input blocks: ");
        
        //Tested upto this point
        //printUncodedPackets(Arrays.asList(inputPackets), payloadLen);

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
        	 byte[] data=intToByte(temp);
        	 
        	try {
        	    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File("D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/testenc.txt"), true));
 				out.write(data, 0, payloadLen);
 				out.close();
        	} catch (Exception e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        	
        }

       
     // printCodedPackets(Arrays.asList(networkOutput), payloadLenCoeffs);
      
        /* decode the received packets */
        PacketDecoder decoder = new PacketDecoder(ff, blockNumber, payloadLen);
        System.out.println(" Decoded packets: ");
        List<UncodedPacket> packets = null;
        for ( int i = 0; i < blockNumber ; i++) {
        	
            packets = decoder.addPacket(networkOutput[i]);
            
            if(packets.size() == blockNumber) {           	
            	
            	System.out.println("No. of coded packets required for decoding: "+i);
            }
            else
            	System.out.println(packets.size());
            
        }
        printUncodedPackets(packets, payloadLen);
        
        System.out.println(" Input blocks: ");
        //printUncodedPackets(Arrays.asList(inputPackets), payloadLen);
    }

   
    private static void printUncodedPackets(List<UncodedPacket> packets, int payloadLen) throws IOException {
    	
 
    	 	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	    //ObjectOutputStream oos = new ObjectOutputStream(bos);
    	    //oos.writeObject(packets);
    	    for(UncodedPacket u:packets) {
    	    	bos.write(u.getPayload());
    	    }    	    
    	    byte[] data = bos.toByteArray();
    	  
    	    try {
       			FileOutputStream out = new FileOutputStream(new File("D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/resdec.txt"), false);
       			out.write(data,0,data.length);
       			out.close();
       		} 
              catch (Exception e) 
              {
       			// TODO Auto-generated catch block
       			e.printStackTrace();
       		}
    	  
    	/*ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream("D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/result.txt")); 

    			stream.writeObject(packets); 
    			stream.close();
    	  */
        	
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
}

