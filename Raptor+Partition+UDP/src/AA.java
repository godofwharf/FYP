
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

public class AA {
	
	public byte[][] AAcalculate(byte[][] A, int K, int[] ESIs, int[] d, int[] a, int[] b, int Lp)
	{
		
		int L=A[1].length; //for column size. if we need row size just Aarg.length is enough
		int N=ESIs.length;
		
		
		byte [][] GLTnew= new byte[N][L];
		byte [][] Anew= new byte[(L-K)+N][L];
		
		
		int tem=0;

		for (int jj=0; jj<N; jj++)
		{
			tem=b[ESIs[jj]];
			while (tem>=L)
				tem=((tem+a[ESIs[jj]]) % Lp);

			GLTnew[jj][tem]= 1;

			int SS=Math.min(d[ESIs[jj]]-1, L-1);

			for (int i=0; i<SS; i++)
			{
				tem=((tem+a[ESIs[jj]]) % Lp);
				while (tem>=L)
					tem=((tem+a[ESIs[jj]]) % Lp);

				GLTnew[jj][tem]= 1;
			}

		}
		
		int pp=-1;
		for (int i=0; i<(L-K)+N ; i++)
		{
			if (i>=L-K)
				pp++;
		
			for (int j=0; j<L; j++)
			{
				
				if (i<L-K)
					Anew[i][j]=A[i][j];
				
				if (i>=L-K)
					Anew[i][j] =GLTnew[pp][j];
								
			}
		}
		
		return Anew;
		
		
	}
}
		
		//*******************************************************************************

//			for (int jj=0; jj<N; jj++)
//			{
//				int blt=b[ESIs[jj]];
//				
//				while (blt>=L)
//					blt=(blt+a[ESIs[jj]]) % Lp;
//					
//				GLTnew[jj][blt+1]=1;
//				SS=Math.min(d[ESIs[jj]]-1, L-1);
//				
//				for (int i=1; i<SS; i++)
//				{
//					blt=(blt+a[ESIs[jj]]) % Lp;
//					
//					while (blt>=L)
//						blt=(blt+a[ESIs[jj]]) % Lp;
//					
//					GLTnew[jj][blt+1]= 1;
//												
//				}
//				
//			}
		
		
		
		
			
		
		
		



