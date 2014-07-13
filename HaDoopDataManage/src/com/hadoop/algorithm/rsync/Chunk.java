package com.hadoop.algorithm.rsync;
/**
 * *文件分块的定义
 * @author MCG
 *
 */
public class Chunk {
	private int weakCheckSum;      //弱校验和
	private String strongCheckSum; //强校验和
	private int index;             //块的编号
	private int size;              //块的大小
	
	
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
