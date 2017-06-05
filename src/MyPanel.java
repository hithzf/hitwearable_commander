import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import javax.sound.sampled.AudioFileFormat;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

class MyPanel extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String BLANKSOURCE = "    ";
	private static final String BLANKCONTENT = "        ";

	SocketUtil socketUtil = new SocketUtil();//用于文件传输
	
	MediaUtil mediaUtil = new MediaUtil();//用于录制音频等操作
	
	static String filePath;//语音、图片文件夹
	
	int mTextLocalPort;//本地的文本接收的端口号
	int mTextDestinationPort ;//目标端的文本接收的端口号
	
	String mDestinationIP ;//目标端的IP地址
	
	int mFileLocalPort ;//本地的文件发送的端口号
	int mFileDestinationPort ;//目标端的文件发送的端口号

	//定义各个控件
	MyJButton b_settings = new MyJButton("设置", 0);
	MyJButton b_connect = new MyJButton("连接", 0);
	
	static JTextPane messageHistory = new JTextPane();//消息记录
	JLabel l_name = new JLabel("单兵：");
	JTextField name = new JTextField(28);
	JTextArea chatting = new JTextArea(5,42);//文本输入框
	
	MyJButton b_sendfile = new MyJButton("开始录制", 0);
	MyJButton b_send = new MyJButton("发送条密", 0);
	
	JScrollPane scro_his = new JScrollPane(messageHistory);//设置带滑动条的信息显示区
	JScrollPane scro_chat = new JScrollPane(chatting);//设置带滑轮的打字区
	
	public MyPanel()
	{
		JPanel panel = new JPanel();
		
		//将各个控件加入到panal中
		b_settings.setPreferredSize(new Dimension(70, 35));
		add(b_settings);
		b_connect.setPreferredSize(new Dimension(70, 35));
		add(b_connect);
		
		add(scro_his);
		scro_his.setPreferredSize(new Dimension(465,450));
		add(scro_chat);
		
		b_sendfile.setPreferredSize(new Dimension(80, 40));
		add(b_sendfile);
		b_send.setPreferredSize(new Dimension(80, 40));
		add(b_send);
		
		add(panel);
		//设置消息框和聊天框
		chatting.setBorder(BorderFactory.createLineBorder(Color.gray,1));
		messageHistory.setBorder(BorderFactory.createLineBorder(Color.gray,1));
//		messageHistory.setPreferredSize(new Dimension(465,450));
		messageHistory.setVisible(true);
		messageHistory.setEditable(false);
		chatting.setVisible(true);
		chatting.setEditable(true);
		//监听器
		b_settings.addActionListener(this);
		b_send.addActionListener(this);
		b_connect.addActionListener(this);
		b_sendfile.addActionListener(this);
		
		//初始化聊天记录
		try {
			List<Msg> list = SQLiteUtil.queryAll();
			for (final Msg msg : list) {
				String item_source;
				if(msg.getType() == Msg.TYPE_RECEIVED){
					item_source = "    单兵    " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.getTime());
					insertDocument(item_source + "\n" + BLANKCONTENT, Color.BLUE);
				}
				else{
					item_source = "    指挥中心    " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.getTime());
					insertDocument(item_source + "\n" + BLANKCONTENT, Color.ORANGE);
				}
				switch (msg.getCatagory()) {
				case Msg.CATAGORY_VOICE:
					MyJButton b_play = new MyJButton("语音消息", 0, 12);
					b_play.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO 实现语音播放
							System.out.println(msg.getPath());
							if(msg.getType() == Msg.TYPE_RECEIVED){
								MediaUtil.openAMR(msg.getPath());
							}else{
								MediaUtil.playAudio(msg.getPath());
							}
						}
					});
					messageHistory.setCaretPosition(messageHistory.getDocument().getLength());
					messageHistory.insertComponent(b_play);
					break;
				case Msg.CATAGORY_TEXT:
					insertDocument(msg.getPath(), Color.BLACK);
					break;
				case Msg.CATAGORY_IMAGE:
					//插入图片
					ImageIcon imageIcon = new ImageIcon(msg.getPath());    // Icon由图片文件形成
					Image image = imageIcon.getImage();                         // 但这个图片太大不适合做Icon
					Image smallImage = image.getScaledInstance(250,300,Image.SCALE_FAST);
					ImageIcon smallIcon = new ImageIcon(smallImage);
					
					messageHistory.setCaretPosition(messageHistory.getDocument().getLength());
					messageHistory.insertIcon(smallIcon);
					break;
				case Msg.CATAGORY_VIDEO:
					MyJButton b_open = new MyJButton("视频消息", 0, 12);
					b_open.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							System.out.println(msg.getPath());
							MediaUtil.openVideo(msg.getPath());
						}
					});
					messageHistory.setCaretPosition(messageHistory.getDocument().getLength());
					messageHistory.insertComponent(b_open);
					break;
				default:
					break;
				}
				insertDocument("\n", Color.BLACK);
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		//将Information.txt文件中的端口以及IP地址信息读到各个变量中
		try
		{
			BufferedReader in = new BufferedReader(new FileReader("Informatin.txt")); 
			mTextLocalPort = Integer.parseInt(in.readLine());
			mTextDestinationPort = Integer.parseInt(in.readLine());
			mDestinationIP = in.readLine();
			mFileLocalPort = Integer.parseInt(in.readLine());
			mFileDestinationPort = Integer.parseInt(in.readLine());
			in.close(); 
		}
		catch(IOException e){
			e.printStackTrace();
		}
		//指定音频文件夹/图片文件夹
		File directory = new File(".");
		try {
			filePath = directory.getCanonicalPath() + "\\voice";
		} catch (IOException e) {
			e.printStackTrace();
		}
		//打开文件接收线程/文本接受线程
		socketUtil.receiveFileBySocket(mDestinationIP, mFileDestinationPort, filePath);
		socketUtil.receiveTextByDatagram(mTextLocalPort);
	}
	
	/**
	 * 聊天记录框中新增一条消息
	 * @param msg
	 */
	public static void updateMessageHistory(final Msg msg){
		String item_source;
		if(msg.getType() == Msg.TYPE_RECEIVED){
			item_source = "    单兵    " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.getTime());
			insertDocument(item_source + "\n" + BLANKCONTENT, Color.BLUE);
		}
		else{
			item_source = "    指挥中心    " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.getTime());
			insertDocument(item_source + "\n" + BLANKCONTENT, Color.ORANGE);
		}
		switch (msg.getCatagory()) {
		case Msg.CATAGORY_VOICE:
			MyJButton b_play = new MyJButton("语音消息", 0, 12);
			b_play.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO 实现语音播放
					System.out.println(msg.getPath());
					if(msg.getType() == Msg.TYPE_RECEIVED){
						MediaUtil.openAMR(msg.getPath());
					}else{
						MediaUtil.playAudio(msg.getPath());
					}
				}
			});
			messageHistory.setCaretPosition(messageHistory.getDocument().getLength());
			messageHistory.insertComponent(b_play);
			break;
		case Msg.CATAGORY_TEXT:
			insertDocument(msg.getPath(), Color.BLACK);
			break;
		case Msg.CATAGORY_IMAGE:
			//插入图片
			ImageIcon imageIcon = new ImageIcon(msg.getPath());    // Icon由图片文件形成
			Image image = imageIcon.getImage();                         // 但这个图片太大不适合做Icon
			Image smallImage = image.getScaledInstance(250,300,Image.SCALE_FAST);
			ImageIcon smallIcon = new ImageIcon(smallImage);
			
			messageHistory.setCaretPosition(messageHistory.getDocument().getLength());
			messageHistory.insertIcon(smallIcon);
			break;
		case Msg.CATAGORY_VIDEO:
			MyJButton b_open = new MyJButton("视频消息", 0, 12);
			b_open.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println(msg.getPath());
					MediaUtil.openVideo(msg.getPath());
				}
			});
			messageHistory.setCaretPosition(messageHistory.getDocument().getLength());
			messageHistory.insertComponent(b_open);
			break;
		default:
			break;
		}
		insertDocument("\n", Color.BLACK);
	}
	
	// 根据传入的颜色及文字，将文字插入文本域
	public static void insertDocument(String text, Color textColor)
	{
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, textColor);// 设置文字颜色
		StyleConstants.setFontSize(set, 12);// 设置字体大小
		Document doc = messageHistory.getStyledDocument();
		
		try {
			doc.insertString(doc.getLength(), text, set);// 插入文字
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) 
	{
		//监听器：设置
		if(e.getSource() == b_settings)
		{
			MyDialog dialog = new MyDialog();
		}
		//监听器：开始录制
		if(e.getSource() == b_sendfile)
		{
			if(b_sendfile.getText().equals("开始录制")){
				mediaUtil.startRecord();
				b_sendfile.setText("结束录制");
			}else if(b_sendfile.getText().equals("结束录制")){
				mediaUtil.stopRecord();
				b_sendfile.setText("发送语音");
			}else{
				String savePath = new StringBuilder(filePath).append("\\").append(UUID.randomUUID()).append(".wav").toString();
				mediaUtil.saveToFile(savePath, AudioFileFormat.Type.WAVE);
				socketUtil.sendFileBySocket(mFileLocalPort, savePath);
				b_sendfile.setText("开始录制");
			}
		}
		//监听器：发送文本
		if(e.getSource() == b_send)
		{
			try
			{
				String messageSend = chatting.getText();
				socketUtil.sendTextByDatagram(messageSend, mDestinationIP, mTextDestinationPort);
				chatting.setText("");
			}
			catch(Exception se)
			{
				se.printStackTrace();
			}
		}
	}

}
