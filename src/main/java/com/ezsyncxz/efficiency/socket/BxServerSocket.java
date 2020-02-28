package com.ezsyncxz.efficiency.socket;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;


/**
 * 接收文件服务
 * @author admin_Hzw
 *
 */
public class BxServerSocket {
	
	/**
	 * 工程main方法
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			final ServerSocket server = new ServerSocket(48123);
			Thread th = new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {  
							System.out.println("开始监听...");
							/*
							 * 如果没有访问它会自动等待
							 */
							Socket socket = server.accept();
							System.out.println("有链接");
							receiveFile(socket);
						} catch (Exception e) {
							System.out.println("服务器异常");
							e.printStackTrace();
						}
					}
				}
			});
			th.run(); //启动线程运行
		} catch (Exception e) {
			e.printStackTrace();
		}     
	}
 
	public void run() {
	}
 
	/**
	 * 接收文件方法
	 * @param socket
	 * @throws IOException
	 */
	public static void receiveFile(Socket socket) throws IOException {
		byte[] inputByte = null;
		int length = 0;
		DataInputStream dis = null;
		FileOutputStream fos = null;
		try {
			try {
				dis = new DataInputStream(socket.getInputStream());
				/*
				 * 文件存储位置  
				 */
				int len = dis.readInt();
				byte[] bytes = new byte[len];
				dis.read(bytes);
				java.lang.String filePath = new java.lang.String(bytes);

				fos = new FileOutputStream(new File(filePath));
				inputByte = new byte[1024];   
				System.out.println("开始接收数据...");  
				while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
					fos.write(inputByte, 0, length);
					fos.flush();    
				}
				System.out.println("完成接收："+"");
			} finally {
				if (fos != null)
					fos.close();
				if (dis != null)
					dis.close();
				if (socket != null)
					socket.close(); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}