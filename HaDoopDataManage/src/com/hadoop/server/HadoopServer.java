package com.hadoop.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HadoopServer {
	private static boolean flag = true;
	public static void main(String[] args){
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(9527);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("�����������ɹ�");
		while(flag){
			try {
				Socket socket = server.accept();
				System.out.println("["+socket.getRemoteSocketAddress()+"] �ɹ������Ϸ����� !");
				new HadoopServerThread(socket).start();
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}
