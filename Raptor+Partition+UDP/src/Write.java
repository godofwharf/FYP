
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class Write {
	
	public void WriteCalculate(byte H[][], long Size, int SYMBOL_SIZE, int K)
	{
		
		try {
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File("D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/result.mp4"), true));
			
			for (int i=0; i< K; i++)
			{
				out.write(H[i], 0, SYMBOL_SIZE);	
			}
			out.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void WriteCalculate1(byte H[][], long Size, int SYMBOL_SIZE, int K)
	{
		
		try {
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File("D:/Apitu/Apitu/college stuff/FINAL YEAR PROJECT/enc.mp4"), true));
			
			for (int i=0; i< K; i++)
			{
				out.write(H[i], 0, SYMBOL_SIZE);	
			}
			out.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
}

