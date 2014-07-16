package com.hadoop.algorithm.rsync;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ²¹¶¡
 * @author MCG
 *
 */
public class Patch implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<PatchPart> patchParts = new ArrayList<PatchPart>();

	/**
	 * ÏòpatchPartsÌí¼ÓpatchPart
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
