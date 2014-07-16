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
 * �ͻ���
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
	
		System.out.println("�ɹ������Ϸ�������");
		//1����ʾ������
		showMainMenu();
		//2���ɹ���½���������������߳�
		new HadoopClientMonitorThread(queue, filePath).start();
		
		//3
		new HadoopCLientThread(socket, oos, ois, queue, account).start();
	}
	
	
	
	public static void showMainMenu() throws Exception{
		while(true){
			System.out.println("---------------------");
			System.out.println("- Hadoop System 0.1 -");
			System.out.println("-  ���������ѡ��          -");
			System.out.println("-  0�������˻�                  -");
			System.out.println("-  1����¼                            -");
			System.out.println("-  x���˳�                            -");
			System.out.println("---------------------");
			String input = scanner.nextLine();
			if("0".equals(input)){
				System.out.println("����������˻���");
				String name = scanner.nextLine();
				System.out.println("������������룺");
				String password1 = scanner.nextLine();
				System.out.println("���ظ�������룺");
				String password2 = scanner.nextLine();
				if(password1.equals(password2)){
					// �����������ע����Ϣ
					System.out.println("����ע����Ϣ");
					Account acct = new Account(name, password1);
					HadoopMessage msg = new HadoopMessage();
					msg.setMsgType(HadoopMessageType.ACCOUNT_CREATE);
					msg.setAcct(acct);
					msg.setContent("�û�ע��");		
					oos.writeObject(msg);
						
					HadoopMessage rMsg = (HadoopMessage)ois.readObject();
					if(rMsg.getMsgType() == HadoopMessageType.SUCCESS){
						System.out.println("�û� ["+name+"] ���ע��");
						continue;
					}else{
						System.out.println("�û� ["+name+"] ע��ʧ�ܣ�ʧ��ԭ��" + rMsg.getContent());
						continue;
					}
				}else{
					System.out.println("�������벻һ��");
					continue;
				}
			}else if("1".equals(input)){
				System.out.println("����������˻���");
				String name = scanner.nextLine();
				System.out.println("������������룺");
				String password1 = scanner.nextLine();
				// ���͵�½����Ϣ
				System.out.println("���͵�½����Ϣ");
				
				Account acct = new Account(name, password1);
				HadoopMessage msg = new HadoopMessage();
				msg.setMsgType(HadoopMessageType.ACCOUNT_LOGIN);
				msg.setAcct(acct);
				msg.setContent("�û���¼");
				oos.writeObject(msg);
				HadoopMessage rMsg = (HadoopMessage)ois.readObject();
				if(rMsg.getMsgType() == HadoopMessageType.SUCCESS){
					System.out.println("�ɹ���¼");
					account = acct;
					break;
				}else{
					System.out.println("��¼ʧ�ܣ�ʧ��ԭ��" + rMsg.getContent());
					continue;
				}			
			}else if("x".equals(input.toLowerCase())){
				System.out.println("�û��˳�ϵͳ");
				HadoopMessage msg = new HadoopMessage();
				msg.setMsgType(HadoopMessageType.ACCOUNT_LOGOUT);
				oos.writeObject(msg);
				HadoopMessage rMsg= (HadoopMessage)ois.readObject();
				System.out.println(rMsg.getContent());
			}else{
				System.out.println("������Ч");
			}
		}	
	}
}
