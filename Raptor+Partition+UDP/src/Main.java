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

import java.io.IOException;
import java.io.*;
import java.util.*;

public class Main {

	public static void main(String[] args) 
	
	{
		
		
		for (int r=0; r<1; r++)
		{
		
		int K=600;
		int SYMBOL_SIZE=128;
		int BLOCK_SIZE=K*SYMBOL_SIZE;
		Scanner inp=new Scanner(System.in);
		
		System.out.println("Give the name with location of the video file");
		String s=inp.nextLine();
		File f=new File(s);
				
		Partition Part=new Partition();
	
		try {
			Part.PartitionCalculate(K,SYMBOL_SIZE,BLOCK_SIZE, r,f,s);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println();
		//long endtime=System.currentTimeMillis();
		//System.out.println("Total elapsed time in execution of this program is :" + (endtime-starttime) );
		}
	}
}
		
			