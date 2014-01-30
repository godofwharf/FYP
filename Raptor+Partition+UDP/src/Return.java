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

 public class Return{
	 
			byte m[][]; //=new byte[2*600][128];
			int indcs[];
		
			public Return(byte T[][], int ind[])
			
			{
				
				this.m=T;
				this.indcs=ind;
			}

			public byte[][] getfirst(){
				return m;
			}
			

			public int[] getsecond(){
				return indcs;
			}

}
