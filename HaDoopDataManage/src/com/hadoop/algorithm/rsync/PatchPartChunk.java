package com.hadoop.algorithm.rsync;
/**
 * 特殊的补丁块，里面存放的食欲服务器一致的数据
 * 所以只需要存放编号
 * @author MCG
 *
 */
public class PatchPartChunk extends PatchPart{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int Index;// 编号
	private int size; // size
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getIndex() {
		
		return Index;
	}

	public void setIndex(int index) {
		this.Index = index;
	}
	
	
}
