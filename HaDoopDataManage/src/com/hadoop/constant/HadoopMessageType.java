package com.hadoop.constant;
/**
 * ��Ϣ����
 * @author MCG
 *
 */
public class HadoopMessageType {
	// ������Ϣ����
	public static final int ERROR = 0;
	public static final int SUCCESS = 1;
	
	// �û�������Ϣ
	public static final int ACCOUNT_CREATE = 10;
	public static final int ACCOUNT_LOGIN = 11;
	public static final int ACCOUNT_LOGOUT = 12;
	
	// ͬ����Ϣ����
	public static final int RSYNC_CHECKSUM = 100;
	public static final int RSYNC_PATCH = 101;
	public static final int RSYNC_NEW = 102;
}
