package com.hadoop.algorithm.rsync;
/**
 * *�ļ��ֿ�Ķ���
 * @author MCG
 *
 */
public class Chunk {
	private int weakCheckSum;      //��У���
	private String strongCheckSum; //ǿУ���
	private int index;             //��ı��
	private int size;              //��Ĵ�С
	
	
	public int getWeakCheckSum() {
		return weakCheckSum;
	}
	
	public void setWeakCheckSum(int weakCheckSum) {
		this.weakCheckSum = weakCheckSum;
	}
	
	public String getStrongCheckSum() {
		return strongCheckSum;
	}
	
	public void setStrongCheckSum(String strongCheckSum) {
		this.strongCheckSum = strongCheckSum;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
}
