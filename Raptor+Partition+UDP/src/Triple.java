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


public class Triple {
	
	public int[][] TripleCalculate(int K, int Lp, int Pr)
	{
		
		Function Func=new Function();
		int[] a = new int[Pr];
		int[] b = new int[Pr];
		int[] d = new int[Pr];

		for (int i=0; i<Pr; i++)
		{
			a[i]=0;
			b[i]=0;
			d[i]=0;
		}		
		
				
		for (int i=1; i<=Pr; i++)
		{
			int[] v =Func.tn(K, i, Lp);
			
			d[i-1]=v[0];
			a[i-1]=v[1];
			b[i-1]=v[2];
		
		}
		
		
		int[][] Z=new int[][]{d,a,b};
		return Z;
	}

}
