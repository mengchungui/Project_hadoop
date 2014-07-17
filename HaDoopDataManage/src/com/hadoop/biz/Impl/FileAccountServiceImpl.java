package com.hadoop.biz.Impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.hadoop.biz.AccountService;
import com.hadoop.entity.Account;

@SuppressWarnings("unchecked")
public class FileAccountServiceImpl implements AccountService{

	private static ObjectInputStream ois;
	private static ObjectOutputStream oos;
	private static String fileName = "data/user.dat";
	private static Map<String, Account> accts = new HashMap<String, Account>();
	
	static {
		try {
			File file = new File(fileName);		
			if(file.exists()){
				ois = new ObjectInputStream(new FileInputStream(fileName));
				accts = (Map<String, Account>)ois.readObject();		
			}	
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(ois != null)
					ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean addAccount(Account acct) {
		accts.put(acct.getName(), acct);
		try {
			File file = new File(fileName);
			if(!file.exists()){
				file.createNewFile();
			}
			oos = new ObjectOutputStream(new FileOutputStream(fileName));
			oos.writeObject(accts);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(oos != null)
					oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public Account queryAccountByName(String name) {
		return accts.get(name);
	}

}
