import java.awt.Color;
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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

/**
 * 网络传输工具类
 * @author 
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
				        
						String fileName = dataInputStream.readUTF();
				        String savePath = new StringBuilder(filePath).append("\\").append(fileName).toString();
				        System.out.println(savePath);
				        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savePath))));
				        
				        int bufferSize = 1024;
				        byte[] buf = new byte[bufferSize];
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
				        //后缀名不同，消息种类不同
				        String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
				        int catagory = 0;
				        switch(prefix){
				        case "amr":
				        	catagory = Msg.CATAGORY_VOICE;
				        	break;
				        case "jpg":
				        	catagory = Msg.CATAGORY_IMAGE;
				        	break;
				        case "mp4":
				        	catagory = Msg.CATAGORY_VIDEO;
				        default:
				        	break;
				        }
				        //数据库操作
				        Msg msg = new Msg(savePath, Msg.TYPE_RECEIVED, System.currentTimeMillis(), catagory);
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
		            
		            File tempFile = new File(filePath);
		            dataOutputStream.writeUTF(tempFile.getName());
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
		                       
		            //数据库操作
			        Msg msg = new Msg(filePath, Msg.TYPE_SENT, System.currentTimeMillis());
			        SQLiteUtil.insert(msg);
			        //UI更新
			        MyPanel.updateMessageHistory(msg);
			        
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
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	/**
	 * 接收文本
	 * @param textLocalPort
	 */
	public void receiveTextByDatagram(final int textLocalPort){
		new Thread() 
		{
			public void run() 
			{
				while(true)
				{
					try
					{
						DatagramSocket ds = new DatagramSocket(textLocalPort);
						byte[] buf = new byte[1024];
						DatagramPacket dp = new DatagramPacket(buf,buf.length);
						ds.receive(dp);
						
						//数据库操作
				        Msg msg = new Msg(new String(dp.getData(), 0, dp.getLength(), "UTF-8"), Msg.TYPE_RECEIVED, System.currentTimeMillis(), Msg.CATAGORY_TEXT);
				        try {
							SQLiteUtil.insert(msg);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
				        //UI更新
				        MyPanel.updateMessageHistory(msg);
						ds.close();
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	/**
	 * 发送文本
	 * @param messageSend
	 * @param targetIP
	 * @param textTargetPort
	 */
	public void sendTextByDatagram(final String messageSend, final String targetIP, final int textTargetPort){
		new Thread() 
		{
			public void run() 
			{
				try
				{
					DatagramSocket ds = new DatagramSocket();
					DatagramPacket dp = new DatagramPacket(messageSend.getBytes(),messageSend.getBytes().length,
																									InetAddress.getByName(targetIP), textTargetPort);
					ds.send(dp);
					
					//数据库操作
			        Msg msg = new Msg(messageSend, Msg.TYPE_SENT, System.currentTimeMillis(), Msg.CATAGORY_TEXT);
			        try {
						SQLiteUtil.insert(msg);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
			        //UI更新
			        MyPanel.updateMessageHistory(msg);
			        
					ds.close();
				}
				catch(Exception se)
				{
					se.printStackTrace();
				}
			}
		}.start();
	}
}
