package com.hadoop.biz;

import com.hadoop.entity.Account;

/**
 * �û��Ĺ���ӿ�
 * @author MCG
 *
 */
public interface AccountService {

	public boolean addAccount(Account acct);
	public Account queryAccountByName(String name);
	
}
