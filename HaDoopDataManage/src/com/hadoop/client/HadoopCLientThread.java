package com.hadoop.client;

import java.util.List;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;


import com.hadoop.algorithm.rsync.Chunk;
import com.hadoop.algorithm.rsync.Patch;
import com.hadoop.algorithm.rsync.Rsync;
import com.hadoop.constant.HadoopMessageType;
import com.hadoop.entity.Account;
import com.hadoop.entity.HadoopMessage;
import com.hadoop.entity.Task;

public class HadoopCLientThread extends Thread{
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private BlockingQueue<Task> queue;
	private Account account;
	private Map<String, Long> matchDatas;
	
	public HadoopCLientThread(Socket socket, ObjectOutputStream oos, ObjectInputStream ois, BlockingQueue<Task> queue, Account account, Map<String, Long> matchDatas){
		this.socket = socket;	
		this.oos = oos;
		this.ois = ois;	
		this.queue = queue;
		this.account = account;
		this.matchDatas = matchDatas;
	}
	
	public void run() {
		while(true){
			try {
				Task task = queue.take();
				if(task.getFileType() == 1){// 普通文件
					//1、需要从服务器得到cheksum
					System.out.println("获取到任务 "+task.getSourceFilePath());
					HadoopMessage msg = new HadoopMessage();
					msg.setMsgType(HadoopMessageType.RSYNC_CHECKSUM);
					msg.setAcct(account);
					msg.setFileName(task.getFilePathString());
					msg.setSourceFileName(task.getSourceFilePath());
					msg.setFileLength(task.getSize());
					oos.writeObject(msg);
					
					//2、利用Rsync算法产生Patch
					HadoopMessage rMsg = (HadoopMessage)ois.readObject();
					Map<Integer, List<Chunk>> checkSumsMap = rMsg.getChunks();
					Patch patch = Rsync.createPatch(checkSumsMap, task.getSourceFilePath(), 0, task.getSize());
					
					//3、向服务器发送patch
					HadoopMessage msg1 = new HadoopMessage();
					msg1.setMsgType(HadoopMessageType.RSYNC_PATCH);
					msg1.setAcct(account);
					msg1.setFileName(task.getFilePathString());
					msg1.setSourceFileName(task.getSourceFilePath());
					msg1.setFileLength(task.getSize());
					msg1.setPatch(patch);
					oos.writeObject(msg1);
				}else if(task.getFileType() == 0){
					//只需要告诉服务器创建一个对应的目录就可以了.
					HadoopMessage msg = new HadoopMessage();
					msg.setMsgType(HadoopMessageType.RSYNC_DIR);
					msg.setAcct(account);
					msg.setFileName(task.getFilePathString());
					msg.setSourceFileName(task.getSourceFilePath());
					msg.setFileLength(task.getSize());
					oos.writeObject(msg);
				}
					
				//获取到服务器的回复
				HadoopMessage rMsg2 = (HadoopMessage)ois.readObject();
				if(rMsg2.getMsgType() == HadoopMessageType.SUCCESS){
					matchDatas.put(task.getSourceFilePath(), task.getSize());
				}
				System.out.println(rMsg2.getContent());
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public ObjectInputStream getOis() {
		return ois;
	}

	public void setOis(ObjectInputStream ois) {
		this.ois = ois;
	}

	public ObjectOutputStream getOos() {
		return oos;
	}

	public void setOos(ObjectOutputStream oos) {
		this.oos = oos;
	}
}
