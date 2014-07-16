package com.hadoop.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.hadoop.constant.HadoopMessageType;
import com.hadoop.entity.Account;
import com.hadoop.entity.HadoopMessage;
import com.hadoop.entity.Task;

/**
 * 客户端
 * @author MCG
 *
 */
public class HadoopClient {
	
	private static Scanner scanner = new Scanner(System.in);
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	private static String filePath = "E:\\local";
	private static BlockingQueue<Task> queue = new LinkedBlockingDeque<Task>();
	private static Account account;
	
	public static void main(String[] args) throws Exception{
		
		Socket socket = new Socket("127.0.0.1", 9527);
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	
		System.out.println("成功连接上服务器！");
		//1、显示主界面
		showMainMenu();
		//2、成功登陆后到这里启动监听线程
		new HadoopClientMonitorThread(queue, filePath).start();
		
		//3
		new HadoopCLientThread(socket, oos, ois, queue, account).start();
	}
	
	
	
	public static void showMainMenu() throws Exception{
		while(true){
			System.out.println("---------------------");
			System.out.println("- Hadoop System 0.1 -");
			System.out.println("-  请输入你的选择          -");
			System.out.println("-  0：创建账户                  -");
			System.out.println("-  1：登录                            -");
			System.out.println("-  x：退出                            -");
			System.out.println("---------------------");
			String input = scanner.nextLine();
			if("0".equals(input)){
				System.out.println("请输入你的账户：");
				String name = scanner.nextLine();
				System.out.println("请输入你的密码：");
				String password1 = scanner.nextLine();
				System.out.println("请重复你的密码：");
				String password2 = scanner.nextLine();
				if(password1.equals(password2)){
					// 向服务器发送注册消息
					System.out.println("发送注册消息");
					Account acct = new Account(name, password1);
					HadoopMessage msg = new HadoopMessage();
					msg.setMsgType(HadoopMessageType.ACCOUNT_CREATE);
					msg.setAcct(acct);
					msg.setContent("用户注册");		
					oos.writeObject(msg);
						
					HadoopMessage rMsg = (HadoopMessage)ois.readObject();
					if(rMsg.getMsgType() == HadoopMessageType.SUCCESS){
						System.out.println("用户 ["+name+"] 完成注册");
						continue;
					}else{
						System.out.println("用户 ["+name+"] 注册失败，失败原因：" + rMsg.getContent());
						continue;
					}
				}else{
					System.out.println("两次密码不一致");
					continue;
				}
			}else if("1".equals(input)){
				System.out.println("请输入你的账户：");
				String name = scanner.nextLine();
				System.out.println("请输入你的密码：");
				String password1 = scanner.nextLine();
				// 发送登陆的消息
				System.out.println("发送登陆的消息");
				
				Account acct = new Account(name, password1);
				HadoopMessage msg = new HadoopMessage();
				msg.setMsgType(HadoopMessageType.ACCOUNT_LOGIN);
				msg.setAcct(acct);
				msg.setContent("用户登录");
				oos.writeObject(msg);
				HadoopMessage rMsg = (HadoopMessage)ois.readObject();
				if(rMsg.getMsgType() == HadoopMessageType.SUCCESS){
					System.out.println("成功登录");
					account = acct;
					break;
				}else{
					System.out.println("登录失败，失败原因：" + rMsg.getContent());
					continue;
				}			
			}else if("x".equals(input.toLowerCase())){
				System.out.println("用户退出系统");
				HadoopMessage msg = new HadoopMessage();
				msg.setMsgType(HadoopMessageType.ACCOUNT_LOGOUT);
				oos.writeObject(msg);
				HadoopMessage rMsg= (HadoopMessage)ois.readObject();
				System.out.println(rMsg.getContent());
			}else{
				System.out.println("输入无效");
			}
		}	
	}
}
