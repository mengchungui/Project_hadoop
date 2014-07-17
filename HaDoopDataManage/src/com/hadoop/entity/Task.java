package com.hadoop.entity;
/**
 * ÈÎÎñ
 * @author MCG
 *
 * 
 */

public class Task {
	private String sourceFilePath;
	private String filePathString;
	private long size;
	private int fileType;// 0:file;1:content
	
	
	public String getFilePathString() {
		return filePathString;
	}
	public void setFilePathString(String filePathString) {
		this.filePathString = filePathString;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getSourceFilePath() {
		return sourceFilePath;
	}
	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}
	public int getFileType() {
		return fileType;
	}
	public void setFileType(int fileType) {
		this.fileType = fileType;
	}
	
}
