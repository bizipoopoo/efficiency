package com.ezsyncxz.efficiency.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 文件发送客户端主程序
 * @author admin_Hzw
 *
 */
public class BxClient {

	private static final Logger logger = LoggerFactory.getLogger(BxClient.class);

	/**
	 * 程序main方法
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();

		File dire = new File("D:\\chenwj\\dev\\test\\efficiency_src");
		File[] files = dire.listFiles();

		for (File file : files) {
			int length = 0;
			double sumL = 0 ;
			byte[] sendBytes = null;
			Socket socket = null;
			DataOutputStream dos = null;
			FileInputStream fis = null;
			boolean bool = false;
			try {
				long l = file.length();
				socket = new Socket();
				socket.connect(new InetSocketAddress("127.0.0.1", 48123));
				dos = new DataOutputStream(socket.getOutputStream());
				fis = new FileInputStream(file);
				sendBytes = new byte[1024];
				while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
					sumL += length;
					System.out.println("已传输："+((sumL/l)*100)+"%");
					dos.write(sendBytes, 0, length);
					dos.flush();
				}
				//虽然数据类型不同，但JAVA会自动转换成相同数据类型后在做比较
				if(sumL==l){
					bool = true;
				}
			}catch (Exception e) {
				System.out.println("客户端文件传输异常");
				bool = false;
				e.printStackTrace();
			} finally{
				if (dos != null)
					dos.close();
				if (fis != null)
					fis.close();
				if (socket != null)
					socket.close();
			}
			System.out.println(bool?"成功":"失败");
		}
		long end = System.currentTimeMillis();
		System.out.println("总耗时：" + (end - start) + "ms");
	}
}