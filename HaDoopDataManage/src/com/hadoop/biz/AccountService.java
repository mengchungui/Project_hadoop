package com.hadoop.biz;

import com.hadoop.entity.Account;

/**
 * 用户的管理接口
 * @author MCG
 *
 */
public interface AccountService {

	public boolean addAccount(Account acct);
	public Account queryAccountByName(String name);
	
}
