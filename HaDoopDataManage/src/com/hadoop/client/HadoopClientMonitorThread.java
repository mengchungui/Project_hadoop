package com.hadoop.client;

import java.io.File;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.io.FileUtils;

import com.hadoop.entity.Task;

public class HadoopClientMonitorThread extends Thread{
	private BlockingQueue<Task> queue;
	private String filePath;
	private long sleepTime = 10*1000;
	private Map<String, Long> matchDatas;
	
	public HadoopClientMonitorThread(BlockingQueue<Task> queue, String filePath, Map<String, Long> matchDatas) {
		this.queue = queue;
		this.filePath = filePath;
		this.matchDatas = matchDatas;
	}


	public void run() {
		
		while(true){
			File file = new File(filePath);
			if(!file.exists()){
				System.out.println("请设置同步目录");
				break;
			}
			if(queue.isEmpty()){
				System.out.println("队列为空，开始检查目录 :");
				try {
					listfile(new File(filePath));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				System.out.println("队列不为空，开始心跳 ！");
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void listfile(File file) throws Exception {
		if(file.isFile()){
			if(matchDatas.containsKey(file.getCanonicalPath())){
				if(matchDatas.get(file.getCanonicalPath()) == FileUtils.sizeOf(file)){
					return ;
				}
			}
			System.out.println(file.getCanonicalPath());
			Task task = new Task();
			try {
				System.out.println("创建任务 "+ file.getCanonicalPath());
				task.setSourceFilePath(file.getCanonicalPath());
				task.setFilePathString(file.getCanonicalPath().substring(filePath.length()));
				task.setSize(FileUtils.sizeOf(file));
				task.setFileType(1);
				queue.put(task);
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}else if(file.isDirectory()){
			System.out.println(file.getName() + "  " + matchDatas.get(file.getCanonicalPath()) + "  " + FileUtils.sizeOf(file));
			if(matchDatas.containsKey(file.getCanonicalPath())){
				if(matchDatas.get(file.getCanonicalPath()) == file.length()){
					return;
				}
			}
			Task task = new Task();
			try {
				System.out.println("创建任务 "+file.getCanonicalPath());
				task.setSourceFilePath(file.getCanonicalPath());
				task.setFilePathString(file.getCanonicalPath().substring(filePath.length()));
				task.setSize(FileUtils.sizeOf(file));
				task.setFileType(0); //目录
				queue.put(task);
			} catch (Exception e) {
				e.printStackTrace();
			}
			matchDatas.put(file.getCanonicalPath(),FileUtils.sizeOf(file));
			File[] files = file.listFiles();
			if(files != null){
				for(File temp: files){
					listfile(temp);
				}
			}
		}
		
	}

}
