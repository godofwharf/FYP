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


public class Handle{
	public static class Metric {
		 static long SS = 0;
		 static long[] TimeEnc = new long[1000];
		
		 static long MM = 0;
		 static long[] TimeDec = new long[1000];
		
	}
	
	public void HandleCalculate(byte VV[][], int K, int SYMBOL_SIZE, long size, int O, int r)
	{
		
		double Overhead=0.02; //receiver get ??? more
		
		byte XX[][]=new byte[K][SYMBOL_SIZE];
		for (int i=0; i<K; i++)
			for (int j=0; j<SYMBOL_SIZE; j++)
				XX[i][j]=VV[i][j];
		Function Func=new Function();
		int X=1;
		while (X*(X-1)<=2*K)
			  X=X+1;
		int S=1;
		while (S<Math.ceil(0.01*K)+X)
			S=S+1;
		while (Func.Isp(S)==0)
			S++;
		int H=1;
		while (Func.Factor(H) / ((Func.Factor(Math.ceil(H/2)))* Func.Factor((H-Math.ceil(H/2)))) <K+S)
			H=H+1;
		int L=0;
		L=K+S+H;
		int Lp=0;
		Lp=L;
		while (Func.Isp(Lp)==0)
			Lp++;
		int Pr=K+(K/4);
		pps pps1=new pps();
		byte A[][]=pps1.ppscalculate(K, H, L, Lp, S, Pr);
		byte B[][]=pps1.ppscalculate(K, H, L, Lp, S, Pr);
		Triple Triple1=new Triple();
		int[][] U=Triple1.TripleCalculate(K, Lp, Pr);
		
		//Encoding starts from here

		byte[][] e=new byte[Pr][SYMBOL_SIZE];


		
		Encoder Encoder1=new Encoder();
		long starttime=System.currentTimeMillis();
		
		e=Encoder1.EncoderCalculate(K, Lp, A, VV, H, S, SYMBOL_SIZE, Pr, U, r);
		
	
	
	
		
		long endtime=System.currentTimeMillis();
		System.out.println("EncoderCalculate time: " + (endtime - starttime));
		
		
		Metric.TimeEnc[r]=endtime-starttime;
		
		if (r==99)
		{
			for (int i=0; i<100; i++)
			{
				//System.out.print("TimeArray:"+ Metric.TimeEnc[i]);
				Metric.SS=Metric.SS + Metric.TimeEnc[i];
				
				//System.out.print("TimeEnc:"+ TimeEnc[i]);
			}
			System.out.print("SS:"+ Metric.SS);
			System.out.println();
			//System.out.println("Total elapsed time in Encoding is :" + SS );
		}
		
		

		
		//From here Communication with random dropping of the packets start
		
		Return Return1=Func.bec(e, 0.1, SYMBOL_SIZE); //0.3 erasure probability randomly
				
		byte[][] erc=Return1.getfirst();
		int indcs[]=Return1.getsecond();
				
		Write Write2=new Write();		
		Write2.WriteCalculate1(erc, size, SYMBOL_SIZE, K);		
		
		int N=(int) ((1+ Overhead)* K);  // overhead ro sefr miknam vase test, 2ta matrix dar 2taraf bayad yeki bashaavad chon eraure ham 0 hast
		
		
		if (indcs.length<K)
		{
			System.out.print("The number of Encoded sysmbols that Decoder has recieved is not enough which is=");
			System.out.print(indcs.length);
			System.exit(-1);
			// get out of here and finishe
		}
		System.out.print(indcs.length);
		
		int[] ESIs=new int[N];
		
		for (int i=0; i<N; i++)
			ESIs[i]=indcs[i];
		
		byte[][] erc1=new byte[N][SYMBOL_SIZE];
		
		for (int i=0; i<N; i++)
			erc1[i]=erc[ESIs[i]];
		
			
		
		byte[][] y=new byte[K][SYMBOL_SIZE];
		

		
		
		Decoder Decoder1=new Decoder();
		
		
		long starttime2=System.currentTimeMillis();
		
		
		y=Decoder1.DecoderCalculate(K, Lp, B, erc1, H, S, SYMBOL_SIZE, Pr, U, ESIs, r);
		
		long endtime2=System.currentTimeMillis();
		
		Metric.TimeDec[r]=endtime2-starttime2;
		
		if (r==99)
		{
			for (int i=0; i<100; i++)
			{
				//System.out.print("DecTimeArray:"+ Metric.TimeDec[i]);
				Metric.MM=Metric.MM + Metric.TimeDec[i];
				
				//System.out.print("TimeEnc:"+ TimeEnc[i]);
			}
			//System.out.println("Total elapsed time in Encoding is :" + SS );
			System.out.print("MM:"+ Metric.MM);
			System.out.println();
		}
		
		// Decoder finish here
		
		Write Write1=new Write();
		
		Write1.WriteCalculate(y, size, SYMBOL_SIZE, K);
		
		
//		System.out.print("Y=*********************************************************************");
//		System.out.println();
//		for (int i=0; i<K; i++)
//		{
//			for (int j=0; j<SYMBOL_SIZE; j++)
//				System.out.print(y[i][j]+", ");
//			System.out.println();
//		}
			
			
		int W=0;
		System.out.print("***************************************");
		System.out.println();
		for (int i=0; i<y.length; i++)
			for (int j=0; j<SYMBOL_SIZE; j++)
			{
		    if (XX[i][j]!=y[i][j])
		    	W++;
			}
		System.out.print(W+ "+W");
		if (W==0)
			System.out.print("Decoding is Successful in Block : " + O);
		
		
	}

		
		
		
	}
//System.out.print("*********************************************************************");
//System.out.println();
//System.out.print("*********************************************************************");
//System.out.println();
//for (int row=0; row < Aw.length; row++)
//	{
//		for(int col=0; col < Aw[row].length; col++)
//			System.out.print(Aw[row][col]+ ", ");
//	System.out.println();
//	}


