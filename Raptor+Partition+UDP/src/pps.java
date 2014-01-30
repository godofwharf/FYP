
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

public class pps {   
	
	public byte[][] ppscalculate(int K, int H, int L, int Lp, int S, int Pr)
	
	{
		
//		int K=12;
//		int S=7;
//		int H=6;
//		int L=25;
//		int Lp=29;
		
			double Hp=0;  

//			int[] a = new int[2*K];
//			int[] b = new int[2*K];
//			int[] d = new int[2*K];
//
//			for (int i=0; i<2*K; i++)
//			{
//				a[i]=0;
//				b[i]=0;
//				d[i]=0;
//			}		
//			
//			Function Func=new Function();
//			
//			for (int i=1; i<=2*K; i++)
//			{
//				int[] v =Func.tn(K, i, Lp);
//				
//				d[i-1]=v[0];
//				a[i-1]=v[1];
//				b[i-1]=v[2];
//			
//			}

			Function Func=new Function();
			Triple Triple1=new Triple();
			int[][] U=Triple1.TripleCalculate(K, Lp, Pr);
			int[] d=U[0];
			int[] a=U[1];
			int[] b=U[2];
			
			Hp= Math.ceil(H/2);

			//Matrix GL=new Matrix(H, K+S); // the Matrix after new is the name of constructor
			//Matrix GT=new Matrix(K, L);
			//Matrix GH=new Matrix(S, K);

			byte [][] GL= new byte[H][K+S];
			byte [][] GT= new byte[K][L];
			byte [][] GH= new byte[S][K];

			for (int row=0; row < GL.length; row++)
			for(int col=0; col < GL[row].length; col++)
			GL[row][col]=0;

			for (int row=0; row < GT.length; row++)
			for(int col=0; col < GT[row].length; col++)
			GT[row][col]=0;

			for (int row=0; row < GH.length; row++)
			for(int col=0; col < GH[row].length; col++)
			GH[row][col]=0;

//**************************************************************************
			int j=1;
			int kk=0;
			int[] qq=new int[H];
			int sum=0;
			

			while ((kk+1) <= K+S)
			{
				sum=0;
				qq=Func.sh(j, H);        
				for(int i=0; i<H; i++)
					sum=qq[i]+sum;
				int w=H-1;
				if (sum==Hp)
				{
					for(int i=0; i< qq.length; i++){
				
						GL[i][kk]=(byte) qq[w-i];
					}
					kk++;
				}
				j++;

			}

//************************************************************************************
			
			int tem;

			for (int jj=0; jj<K; jj++)
			{
				tem=b[jj];
				while (tem>=L)
					tem=((tem+a[jj]) % Lp);

				GT[jj][tem]= 1;

				
				int SS;
				SS=Math.min(d[jj]-1, L-1);

				for (int ss=0; ss<SS; ss++)
				{
					tem=((tem+a[jj]) % Lp);
					while (tem>=L)
						tem=((tem+a[jj]) % Lp);

					GT[jj][tem]= 1;
				}

			}
			
			
//*******************************************************************************			
			int aa=0;
			int bb=0;

			for (int i=1; i<=K; i++)
			{
				double W=Math.floor((i-1)/S);
								
				aa= (int) (1+ (W % (S-1)));
				bb=i % S;
								
				if (bb==0)
					bb=S;
			
				GH[bb-1][i-1]= 1;
				
				bb=(bb+aa) % S;

				if(bb==0)
					bb=S;

				GH[bb-1][i-1]= 1;

				bb=(bb+aa) % S;

				if(bb==0)
					bb=S;

				GH[bb-1][i-1]= 1;
			
					
		}
//****************************************************************************
//			for (int row=0; row < GL.length; row++)
//			{
//				for(int col=0; col < GL[row].length; col++)
//				System.out.print(GL[row][col]);
//				
//				System.out.println();
//			}
//			
//			for (int row=0; row < GT.length; row++)
//			{
//				for(int col=0; col < GT[row].length; col++)
//					System.out.print(GT[row][col]);
//			System.out.println();
//			}
//			
//			for (int row=0; row < GH.length; row++)
//			{
//				for(int col=0; col < GH[row].length; col++)
//					System.out.print(GH[row][col]);
//			System.out.println();
//			}
			
//*********************************************************************************
			byte [][] eye1= new byte[S][S];
			byte [][] eye2= new byte[H][H];
			byte [][] zero= new byte[S][S];
			byte [][] A= new byte[L][L];
			
			
			for (int row=0; row < eye1.length; row++)
				for(int col=0; col < eye1[row].length; col++)
				{
					if (row==col)
						eye1[row][col]=1;
				}
			

			for (int row=0; row < eye2.length; row++)
				for(int col=0; col < eye2[row].length; col++)
				{
					if (row==col)
						eye2[row][col]=1;
				}
			
			for (int row=0; row < zero.length; row++)
				for(int col=0; col < zero[row].length; col++)
				zero[row][col]=0;
			

			for (int row=0; row < A.length; row++)
				for(int col=0; col < A[row].length; col++)
				A[row][col]=0;

//************************************************************************************
			int t=0;	
			int tt=0;
			int p=-1;
			int pp=-1;
			int ppp=-1;
			for (int i=0; i<L; i++)
			{
			t=0;	
			tt=0;
			if (S<=i && i<S+H)
				p++;
			if (i>= S+H)
				pp++;
			if (S<=i && i<S+H)
				ppp++;
			
			
				for (int jj=0; jj<L; jj++)
				{
					
					if (i<S)
					{
						if (jj<K)
							A[i][jj]=GH[i][jj];

						if (K<=jj && jj<K+S)
							{
							A[i][jj]= eye1[i][t];
							t++;
							}
						

					}


					if (S<=i && i<S+H) 
					{
						if (jj<K+S)
							{
							A[i][jj]= GL[p][jj];
							
							}
						

						if (jj>=K+S)
						{
							A[i][jj]= eye2[ppp][tt];
							tt++;
						}
						
							

					}

					if (i>= S+H)
						{
						A[i][jj]= GT[pp][jj];
						
						}
						
				}
			}

//			System.out.print("*************************************************************");
//			System.out.println();
//			for (int row=0; row < A.length; row++)
//			{
//				for(int col=0; col < A[row].length; col++)
//					System.out.print(A[row][col]);
//			System.out.println();
//			}
//			
//			
			
			return A;
		
	}
}