package com.hadoop.client;

import java.io.File;
import java.util.concurrent.BlockingQueue;

import com.hadoop.entity.Task;

public class HadoopClientMonitorThread extends Thread{
	private BlockingQueue<Task> queue;
	private String filePath;
	private long sleepTime = 30*1000;
	
	public HadoopClientMonitorThread(BlockingQueue<Task> queue, String filePath) {
		this.queue = queue;
		this.filePath = filePath;
	}
	
	public void run() {
		
		while(true){
			File file = new File(filePath);
			if(!file.exists()){
				System.out.println("请设置同步目录");
				break;
			}
			if(queue.isEmpty()){
				listfile(new File(filePath));
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void listfile(File file) {
		if(file.isFile()){
			Task task = new Task();
			try {
				System.out.println("创建任务 "+ file.getCanonicalPath());
				task.setSourceFilePath(file.getCanonicalPath());
				task.setFilePathString(file.getCanonicalPath().substring(filePath.length()));
				task.setSize(file.length());
				queue.put(task);
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}else if(file.isDirectory()){
			File[] files = file.listFiles();
			if(files != null){
				for(File temp: files){
					listfile(temp);
				}
			}
		}
		
	}

}
