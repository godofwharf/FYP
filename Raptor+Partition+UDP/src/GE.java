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

public class GE {
	
	public static class Metric1 {
		 static long Fail = 0;
		
		
	}

	public byte[][] GEcalculate(byte A[][], byte[][] e, int SYMBOL_SIZE, int r )
	{
		int M=0;
		int L=0;
		int N=0;
		
		M=A.length;
		L=A[1].length;
		N=e.length;
		
		
		
		byte[][] D =new byte[M][SYMBOL_SIZE];
		
		byte[][] c = new byte[L][SYMBOL_SIZE];
		
				
		int p=0;
		for (int i=0; i<M; i++)
		{
			if(i<M-N)
				for (int j=0; j<SYMBOL_SIZE; j++)
				    D[i][j]=0;
			
			else
				{
				D[i]=e[p];
				p++;
				}
		}
		
		int[] HI=new int[M];
		int[] LOW=new int[M];
		
		
		for (int jj=0; jj<L; jj++) 
		{
			int k=0;
			for (int i=0; i<=jj-1; i++)
			{
				if (A[i][jj]==1)
				{
					HI[k]=i;
					k++;
				}
			}
			
		int kk=0;
			
		for (int i=jj; i<M; i++)
		{
			if(A[i][jj]==1)
			{
				LOW[kk]=i;
				kk++;
			}
		}
		
		
		
		if (kk==0)
		{
			Metric1.Fail++;
			System.out.print("in GE K is quite small and due to unclear reasons the process can not continue");
			//System.exit(-1);
			if (r==999){
				 System.out.print("The number of Fail:" + Metric1.Fail);
				 System.out.println();
				 }
			return c;
			
		}
		
		
		for (int i=1; i<kk; i++)
		{
			for (int j=0; j<SYMBOL_SIZE; j++)
			D[LOW[i]][j]=(byte) (D[LOW[i]][j] ^ D[LOW[0]][j]);
			
			for (int q=0; q<L; q++)
				A[LOW[i]][q]=(byte) (A[LOW[i]][q] ^ A[LOW[0]][q]);
			
		}
		
		for (int i=0; i<k; i++)
		{
			for (int j=0; j<SYMBOL_SIZE; j++)
			D[HI[i]][j]=(byte) (D[HI[i]][j] ^ D[LOW[0]][j]);
			
			for (int q=0; q<L; q++ )
				A[HI[i]][q]=(byte) (A[HI[i]][q] ^ A[LOW[0]][q]);
		}
		
			
		byte[] temp;
		byte[] tempo = new byte[SYMBOL_SIZE];
		
		temp=A[jj];
		A[jj]=A[LOW[0]];
		A[LOW[0]]=temp;
		
		tempo=D[jj];
		D[jj]=D[LOW[0]];
		D[LOW[0]]=tempo;
									
		}
		 for (int i=0; i<L; i++)
			 c[i]=D[i];
		 
		 
//		 System.out.print("***********************************************");
//			System.out.println();
//			System.out.print("cdc=");
//			for (int i=0; i<L; i++)
//			{
//				for (int j=0; j<SYMBOL_SIZE; j++)
//				System.out.print(c[i][j]+", ");
//			System.out.println();
//			}
		 
		 if (r==999){
			 System.out.print("The number of Fail:" + Metric1.Fail);
			 System.out.println();
			 }
		 
		 return c;
		
		//System.out.print("A=");
		//System.out.println();
		//for(int i=0; i<M; i++)
		//{
		//	for(int jj=0; jj<L; jj++)
		//	{
		//		System.out.print(A[i][jj] + ", ");
		//	}
		//	System.out.println();
			
		//}
		//System.out.print("D=");
		//for (int i=0; i<L; i++)
		//	System.out.print(c[i]+", ");
		
		
	}
}

