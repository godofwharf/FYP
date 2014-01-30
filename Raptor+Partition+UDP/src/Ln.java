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

public class Ln { 
	
	public byte[] Lncalculate(int K, byte[][] c, int d, int a, int b, int L, int SYMBOL_SIZE) 
	{

		int Lp=L;
		Function Func=new Function();
		
		while (Func.Isp(Lp)==0)
			Lp++;
		
		byte[] e = new byte[SYMBOL_SIZE];
				
	while (b>=L)
		b=((b+a) % Lp);

	//GT[jj][tem]= 1;
	for (int j=0; j<SYMBOL_SIZE; j++)
	e[j]=c[b][j];

	int SS=0;
	SS=Math.min(d-1, L-1);

	for (int ss=0; ss<SS; ss++)
	{
		b=((b+a) % Lp);
		while (b>=L)
			b=((b+a) % Lp);

		//GT[jj][tem]= 1;
		for (int j=0; j<SYMBOL_SIZE; j++)
		e[j]=(byte) (e[j] ^ c[b][j]);
	}
	return e;
	}
}


