package com.hadoop.algorithmImpl;

import com.hadoop.algorithm.Algorithm;

public class AlgorithmImpl implements Algorithm{
	
	private int i_mod = 65521;
	
	public int Adler32(byte dataByte[]){	
		int A=1, B=0;
		int length = dataByte.length;
		for(int i=0; i<length; i++){
			A += dataByte[i];
			B += A;
		}
		A %= i_mod;
		B %= i_mod;
	
		return (B<<16) | A;
	}
	
	public int nextAdler32(int alder, byte preByte, byte nextByte, int len){
		int A, B, newA, newB;
		/// 求出原来的A,B
		A = alder & 0xFFFF;
		B = (alder>>16) & 0xFFFF;
		/// 求出新的A,B
		newA = (A - preByte + nextByte)%i_mod;
		newB = (B - len*preByte - 1 + newA)%i_mod;
		
		return (newB<<16) | newA;
	}
	
	public String MD5(byte[] source) {
		String resultString = null;
		
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(source);
			byte datas[] = md.digest();
			
			char str[] = new char[16*2];
			int k = 0;
			
			for(int i=0; i<16; i++){
				byte b = datas[i];
				str[k++] = hexDigits[b>>>4 & 0xf];
				str[k++] = hexDigits[b & 0xf];
			}
			
			resultString = new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultString;
		
	}
}
