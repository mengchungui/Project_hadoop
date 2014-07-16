package com.hadoop.server;

import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import com.hadoop.algorithm.rsync.Chunk;
import com.hadoop.algorithm.rsync.Patch;
import com.hadoop.algorithm.rsync.Rsync;
import com.hadoop.algorithmImpl.AlgorithmImpl;
import com.hadoop.biz.AccountService;
import com.hadoop.biz.Impl.MemeryAccountServiceImpl;
import com.hadoop.constant.HadoopMessageType;
import com.hadoop.entity.Account;
import com.hadoop.entity.HadoopMessage;

public class HadoopServerThread extends Thread{
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private AccountService acctService = new MemeryAccountServiceImpl();
	private static boolean flag = true;
	private static String filePath = "E:\\remote";
		

	public HadoopServerThread(Socket socket) {
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(flag){
			try {
				HadoopMessage msg = (HadoopMessage)ois.readObject();
				
				switch (msg.getMsgType()) {
				case HadoopMessageType.ACCOUNT_CREATE:
					createAccount(msg.getAcct());
					break;
				case HadoopMessageType.ACCOUNT_LOGIN:
					loginAccount(msg.getAcct());
					break;
				case HadoopMessageType.ACCOUNT_LOGOUT:
					loginout(msg.getAcct());
					flag = false;
					break;
				case HadoopMessageType.RSYNC_CHECKSUM:
					calcCheckSum(msg);
					break;
				case HadoopMessageType.RSYNC_PATCH:
					applyPatch(msg);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			} 
		}
	}

	private void applyPatch(HadoopMessage msg) throws Exception {
		String clientFilePath = msg.getFileName();
		Patch patch = msg.getPatch();
		System.out.println(filePath + clientFilePath);
		File file  = new File(filePath + clientFilePath);
		if(!file.exists()){
			File parent = file.getParentFile();
			if(!parent.exists()){
				parent.mkdirs();
			}
			file.createNewFile();
		}
		Rsync.createNewFile(patch, filePath + clientFilePath);
		HadoopMessage msg1 = new HadoopMessage();
		msg1.setMsgType(HadoopMessageType.SUCCESS);
		msg1.setContent("文件同步成功 !");
		oos.writeObject(msg1);	
	}

	private void calcCheckSum(HadoopMessage msg) throws Exception{
		String clientFilePath = msg.getFileName();
		Map<Integer, List<Chunk>> checkSumsMap = Rsync.calcCheckNum(filePath + clientFilePath);
		if(checkSumsMap == null){
			checkSumsMap = new HashMap<Integer,List<Chunk>>();
		}
		HadoopMessage msg1 = new HadoopMessage();
		msg1.setMsgType(HadoopMessageType.RSYNC_CHECKSUM);
		msg1.setChunks(checkSumsMap);
		oos.writeObject(msg1);
	}
	
	private void loginAccount(Account acct) throws Exception {
		Account temp = acctService.queryAccountByName(acct.getName());
		if(temp == null){
			HadoopMessage msg = new HadoopMessage();
			msg.setMsgType(HadoopMessageType.ERROR);
			msg.setContent("此账户不存在！");
			oos.writeObject(msg);
			return;
		}
		
		AlgorithmImpl algorithmImpl = new AlgorithmImpl();
		if(temp.getPassword().equals(algorithmImpl.MD5(acct.getPassword().getBytes()))){
			HadoopMessage msg = new HadoopMessage();
			msg.setMsgType(HadoopMessageType.SUCCESS);
			msg.setContent("服务器校验通过 !");
			oos.writeObject(msg);
		}else{
			HadoopMessage msg = new HadoopMessage();
			msg.setMsgType(HadoopMessageType.ERROR);
			msg.setContent("服务器检验失败");
			oos.writeObject(msg);
		}	
	}

	private void createAccount(Account acct) throws IOException {
		Account temp = acctService.queryAccountByName(acct.getName());
		if(temp == null){
			HadoopMessage msg = new HadoopMessage();
			msg.setMsgType(HadoopMessageType.SUCCESS);
			msg.setContent("成功注册该用户");
			oos.writeObject(msg);
			AlgorithmImpl algorithmImpl = new AlgorithmImpl();
			acct.setPassword(algorithmImpl.MD5(acct.getPassword().getBytes()));
			acctService.addAccount(acct);
		}else{
			HadoopMessage msg = new HadoopMessage();
			msg.setMsgType(HadoopMessageType.ERROR);
			msg.setContent("服务器已存在该用户");
			oos.writeObject(msg);
		}	
	}
	
	private void loginout(Account acct) throws IOException {
		HadoopMessage msg = new HadoopMessage();
		msg.setMsgType(HadoopMessageType.SUCCESS);
		msg.setContent("退出成功");
		oos.writeObject(msg);
		
	}
}
