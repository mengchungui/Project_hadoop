package com.hadoop.entity;

import java.util.List;
import java.io.Serializable;
import java.util.Map;
import com.hadoop.algorithm.rsync.*;
/**
 * ϵͳ�������Ϣ
 * @author MCG
 *
 */
public class HadoopMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// �û�����
	private int msgType;   //��Ϣ����
	private Account acct;  //�˻�
	private String content;//��Ϣ����
	
	

	//�ļ�ͬ��
	private String fileName;// �ļ�����
	private String sourceFileName;//���ص��ļ�����
	private long fileLength;// �ļ���С
	private Map<Integer, List<Chunk>> chunks;// У���
	private Map<String, Long> matchMap;// ƥ����ļ�
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
