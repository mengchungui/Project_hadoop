package com.hadoop.server;

import java.util.HashMap;
import java.util.Map;

import com.hadoop.entity.Account;

public class HadoopServerSaveThread extends Thread{
	private Map<String, Long> matchDatas = new HashMap<String, Long>();
	private String filePath;
	private Account account;
	public HadoopServerSaveThread(String filePath, Map<String, Long> matchDatas,Account account) {
		this.filePath = filePath;
		this.matchDatas = matchDatas;
		this.account = account;
	}
	
	@Override
	public void run(){
		while(true){
			System.out.println(filePath + account.getName() + "\\match.dat");
			HadoopServerMatchFileUtil matchUtil = new HadoopServerMatchFileUtil(filePath + account.getName() + "\\match.dat");
			try {
				System.out.println("刷新匹配文件到硬盘 !");
				matchUtil.saveMatchFile(matchDatas);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(30*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
