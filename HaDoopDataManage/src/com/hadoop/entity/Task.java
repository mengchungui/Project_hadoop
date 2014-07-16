package com.hadoop.entity;
/**
 * хннЯ
 * @author MCG
 *
 * 
 */

public class Task {
	private String sourceFilePath;
	private String filePathString;
	private long size;
	
	
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
	
}
