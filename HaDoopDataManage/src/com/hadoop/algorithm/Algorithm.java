package com.hadoop.algorithm;

public interface Algorithm {
	
	/// adler_32
	public int Adler32(byte dataByte[]);
	/// right move
	public int nextAdler32(int alder, byte preByte, byte nextByte, int len);
	/// MD5
	public String MD5(byte[] source);

}
