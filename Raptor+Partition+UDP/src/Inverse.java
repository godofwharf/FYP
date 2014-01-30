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

public class Inverse { 
	
	public byte[][] Inversecalculate(byte AA[][], byte[][] b, int H, int K, int S, int SYMBOL_SIZE) 
	{
		int L=K+S+H;
		byte[][] D = new byte[L][SYMBOL_SIZE];
		//byte[][] c = new byte[L][SYMBOL_SIZE];
		int P=0;
		int R=0;
		
		
		for (int i=0; i<L; i++)
		{
			if(i<L-K)
				//for (int j=0; j<128; j++)
				  //  D[i][j]=0;
				R++;
				
			
			else
				{
				D[i]=b[P];
				P++;
				}
		}
		
		int[] HI=new int[L];
		int[] LOW=new int[L];
		
		
		for (int jj=0; jj<L; jj++)
		{
			int k=0;
			for (int i=0; i<=jj-1; i++)
			{
				if (AA[i][jj]==1)
				{
					HI[k]=i;
					k++;
				}
			}
			
		int kk=0;
			
		for (int i=jj; i<L; i++)
		{
			if(AA[i][jj]==1)
			{
				LOW[kk]=i;
				kk++;
			}
		}
		
		
		if (kk==0){
			System.out.print(" Encodr K is quite small and due to unclear reasons the process can not continue");
			System.exit(-1);
		}
		
		for (int i=1; i<kk; i++)
		{
			for (int j=0; j<SYMBOL_SIZE; j++)
			{
			D[LOW[i]][j]=(byte) (D[LOW[i]][j] ^ D[LOW[0]][j]);
			}
			
			for (int q=0; q<L; q++)
				AA[LOW[i]][q]=(byte) (AA[LOW[i]][q] ^ AA[LOW[0]][q]);
			
		}
		
		for (int i=0; i<k; i++)
		{
			for (int j=0; j<SYMBOL_SIZE; j++)
			  D[HI[i]][j]=(byte) (D[HI[i]][j] ^ D[LOW[0]][j]);
			
			for (int q=0; q<L; q++ )
				AA[HI[i]][q]=(byte) (AA[HI[i]][q] ^ AA[LOW[0]][q]);
		}
		
			
		byte[] temp;
		byte[] tempo = new byte[SYMBOL_SIZE];
		
		temp=AA[jj];
		AA[jj]=AA[LOW[0]];
		AA[LOW[0]]=temp;
		
		for (int i=0; i<SYMBOL_SIZE; i++)
			tempo[i]=D[jj][i];
		
		for (int i=0; i<SYMBOL_SIZE; i++)
		D[jj][i]=D[LOW[0]][i];
		
		for (int i=0; i<SYMBOL_SIZE; i++)
		D[LOW[0]][i]=tempo[i];
									
		}

		
		return D;
	}
}