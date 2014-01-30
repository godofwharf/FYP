
/*This file is part of Raptor Code.

    Raptor Code is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Raptor Code is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Raptor Code.  If not, see <http://www.gnu.org/licenses/>. */

/*Implemented by: Hossein Rouhani Zeidanloo
Email address:  H_Rouhani@Hotmail.com */

import java.io.*;

public class Partition {
	
	public void PartitionCalculate(int K, int SYMBOL_SIZE, int BLOCK_SIZE, int r, File f,String s ) throws IOException
	{

   // int BLOCK_SIZE = 76800; // 75Kb
    //int SYMBOL_SIZE = 3; // 128bytes
		
		int O=0;
    
    Handle Handle1=new Handle();  
   
    	byte V[][]=new byte[K][SYMBOL_SIZE];
    	//File file=new File("D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/testvid.mp4");  //("F:/workspace/Test.bin");
    	File file=f;
    	long size=file.length();
    	

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(s)));
        
        
        try {
        	
        	int n;
        	
        	do {
        		int k = 0;
        		while ((n = in.read(V[k], 0, SYMBOL_SIZE)) > 0 && k < K-1)
        			k++;
        		
        		O++;
        		
        	//	System.out.println(V[0][1]);   		
        		       		
        			if (n>0 && k==K-1)
        		        Handle1.HandleCalculate(V, K,SYMBOL_SIZE, size, O, r);		
        		
        			else if (n<0 && k>0) //padding 
        			{
        				
                			
                			            			
                				long X=size % (K*SYMBOL_SIZE);
                				int Y=(int) Math.ceil(X/SYMBOL_SIZE);
                				int YY=(int) (X % SYMBOL_SIZE);
                				
                				if (YY!=0)
                				{
                					for (int j=YY; j<SYMBOL_SIZE; j++)
                						V[Y][j]=0;
                					
                					for (int i=Y+1; i<K; i++)
                						for (int j=0; j<SYMBOL_SIZE; j++)
                							V[i][j]=0;
                				}
                				
                				if (YY==0)
                				{
                					for (int i=Y; i<K; i++)
                						for (int j=0; j<SYMBOL_SIZE; j++)
                							V[i][j]=0;
                				}
                				
                			
                			Handle1.HandleCalculate(V, K,SYMBOL_SIZE, size, O, r);
                		//}
                		
                			
        			}
        	} 
        	while (n > 0);	
        		

        }
        finally { 
            in.close();
       }
       // return V; //off
    }

}



