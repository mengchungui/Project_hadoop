package com.hadoop.biz.Impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hadoop.biz.AccountService;
import com.hadoop.entity.Account;

/**
 * 内存的实现
 * @author MCG
 * 
 *
 */
public class MemeryAccountServiceImpl implements AccountService{
	private static Map<String, Account> acctsMap = new ConcurrentHashMap<String, Account>();
	
	
	@Override
	public boolean addAccount(Account acct) {
		acctsMap.put(acct.getName(), acct);
		return true;
	}

	@Override
	public Account queryAccountByName(String name) {
		
		return acctsMap.get(name);
	}

}
