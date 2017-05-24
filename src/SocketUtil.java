import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

/**
 * 网络传输工具
 * @author hzf
 *
 */
public class SocketUtil {
	
	/**
	 * 接收文件
	 * @param targetIP
	 * @param fileReceivePort
	 * @param filePath
	 */
	public void receiveFileBySocket(final String targetIP, final int fileReceivePort, final String filePath){
		new Thread(){
			public void run(){
				while(true){
					try{
						Socket receiveSocket = new Socket(targetIP, fileReceivePort);
						DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(receiveSocket.getInputStream()));
						
				        int bufferSize = 1024;
				        byte[] buf = new byte[bufferSize];
				        
				        String savePath = new StringBuilder(filePath).append("\\").append(UUID.randomUUID()).append(".amr").toString();
				        System.out.println(savePath);
				        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savePath))));
				        while (true){
				            int read = 0;
				            if (dataInputStream != null){
				                read = dataInputStream.read(buf);
				            }
				            if (read == -1){
				                break;    
				            }
				            dataOutputStream.write(buf, 0, read);
				        }
				        System.out.println("接受完毕");
				        //数据库操作
				        Msg msg = new Msg(savePath, Msg.TYPE_RECEIVED, System.currentTimeMillis());
				        SQLiteUtil.insert(msg);
				        //UI更新
				        MyPanel.updateMessageHistory(msg);
				        
				        dataOutputStream.close();
				        dataInputStream.close();
	                    receiveSocket.close();
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}//while
			}
		}.start();
	}
	
	/**
	 * 发送文件
	 * @param sendPort
	 * @param filePath
	 */
	public void sendFileBySocket(final int sendPort, final String filePath){
		new Thread() 
		{
			public void run() 
			{
				ServerSocket serverSocket = null;
				try 
				{
					serverSocket = new ServerSocket(sendPort, 1);
		            System.out.println("等待链接...");
		            Socket sendSocket = serverSocket.accept();
		            System.out.println("完成链接!");
		            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
		            DataOutputStream dataOutputStream = new DataOutputStream(sendSocket.getOutputStream());

		            int bufferSize = 1024;//8kB,以一个UTF字符传送
		            byte[] buffer = new byte[bufferSize];
		                      
		            while (true) 
		            {
		                int readLength = 0;
		                if (dataInputStream != null) 
		                {
		                    readLength = dataInputStream.read(buffer);
		                }
		                if (readLength == -1) 
		                {
		                    break;
		                }
		                dataOutputStream.write(buffer, 0, readLength);
		                   
		            }
		            dataOutputStream.flush();
		                       
		            dataOutputStream.close();
		            dataInputStream.close();
		            sendSocket.close();
		            serverSocket.close();
		        }
				catch(BindException bindException){
		            try {
						serverSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		        catch (IOException e){
		            e.printStackTrace();
		        }
			}
		}.start();
	}
}
