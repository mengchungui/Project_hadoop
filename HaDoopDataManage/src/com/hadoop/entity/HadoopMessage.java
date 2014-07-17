package com.hadoop.entity;

import java.util.List;
import java.io.Serializable;
import java.util.Map;
import com.hadoop.algorithm.rsync.*;
/**
 * 系统定义的消息
 * @author MCG
 *
 */
public class HadoopMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 用户管理
	private int msgType;   //消息类型
	private Account acct;  //账户
	private String content;//消息内容
	
	

	//文件同步
	private String fileName;// 文件名称
	private String sourceFileName;//本地的文件名称
	private long fileLength;// 文件大小
	private Map<Integer, List<Chunk>> chunks;// 校验和
	private Map<String, Long> matchMap;// 匹配的文件
	private Patch patch;

	
	public Map<String, Long> getMatchMap() {
		return matchMap;
	}

	public void setMatchMap(Map<String, Long> matchMap) {
		this.matchMap = matchMap;
	}

	public Map<Integer, List<Chunk>> getChunks() {
		return chunks;
	}

	public Patch getPatch() {
		return patch;
	}

	public void setPatch(Patch patch) {
		this.patch = patch;
	}

	public void setChunks(Map<Integer, List<Chunk>> chunks) {
		this.chunks = chunks;
	}
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public Account getAcct() {
		return acct;
	}

	public void setAcct(Account acct) {
		this.acct = acct;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
