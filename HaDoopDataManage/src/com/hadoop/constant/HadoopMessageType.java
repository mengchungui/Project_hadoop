package com.hadoop.constant;
/**
 * 消息类型
 * @author MCG
 *
 */
public class HadoopMessageType {
	// 基础消息类型
	public static final int ERROR = 0;
	public static final int SUCCESS = 1;
	
	// 用户管理消息
	public static final int ACCOUNT_CREATE = 10;
	public static final int ACCOUNT_LOGIN = 11;
	public static final int ACCOUNT_LOGOUT = 12;
	
	// 同步消息类型
	public static final int RSYNC_CHECKSUM = 100;
	public static final int RSYNC_PATCH = 101;
	public static final int RSYNC_NEW = 102;
}
