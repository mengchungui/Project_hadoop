package com.hadoop.algorithm.rsync;

import java.util.ArrayList;

/**
 * ����
 * @author MCG
 *
 */
public class Patch {
	private ArrayList<PatchPart> patchParts = new ArrayList<PatchPart>();

	/**
	 * ��patchParts���patchPart
	 * 
	 * @param patchPart
	 */
	public void adddPatchPart(PatchPart patchPart){
		this.patchParts.add(patchPart);
	}
	
	public ArrayList<PatchPart> getPatchParts() {
		return patchParts;
	}

	public void setPatchParts(ArrayList<PatchPart> patchParts) {
		this.patchParts = patchParts;
	}
	
}
