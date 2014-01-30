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

public class Decoder {
	
	public byte[][] DecoderCalculate(int K, int Lp, byte B[][], byte erc1[][], int H, int S, int SYMBOL_SIZE, int Pr, int[][] U, int[] ESIs, int r )
	{
		
		int L=K+H+S;
		int[] d=U[0];
		int[] a=U[1];
		int[] b=U[2];
		
		AA AA1=new AA();
		
		byte[][] Aw=AA1.AAcalculate(B, K, ESIs, d, a, b, Lp);
		
		GE GE1=new GE();
		
		byte[][] cdc=GE1.GEcalculate(Aw, erc1, SYMBOL_SIZE, r);
		
		
		byte[][] y=new byte[K][SYMBOL_SIZE];
		
		Ln Ln1=new Ln();
		
		for (int j=0; j<K; j++)  //deghat shavad faghesh ro ba ghesmate ghabl
			y[j]=Ln1.Lncalculate(K, cdc, d[j], a[j], b[j], L, SYMBOL_SIZE);
		
	
		
		return y;
		
		
		
		
	}

}
