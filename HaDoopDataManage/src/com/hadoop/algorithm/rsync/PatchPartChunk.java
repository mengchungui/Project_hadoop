package com.hadoop.algorithm.rsync;
/**
 * ����Ĳ����飬�����ŵ�ʳ��������һ�µ�����
 * ����ֻ��Ҫ��ű��
 * @author MCG
 *
 */
public class PatchPartChunk extends PatchPart{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int Index;// ���
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
