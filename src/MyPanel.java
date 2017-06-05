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

	SocketUtil socketUtil = new SocketUtil();//�����ļ�����
	
	MediaUtil mediaUtil = new MediaUtil();//����¼����Ƶ�Ȳ���
	
	static String filePath;//������ͼƬ�ļ���
	
	int mTextLocalPort;//���ص��ı����յĶ˿ں�
	int mTextDestinationPort ;//Ŀ��˵��ı����յĶ˿ں�
	
	String mDestinationIP ;//Ŀ��˵�IP��ַ
	
	int mFileLocalPort ;//���ص��ļ����͵Ķ˿ں�
	int mFileDestinationPort ;//Ŀ��˵��ļ����͵Ķ˿ں�

	//��������ؼ�
	MyJButton b_settings = new MyJButton("����", 0);
	MyJButton b_connect = new MyJButton("����", 0);
	
	static JTextPane messageHistory = new JTextPane();//��Ϣ��¼
	JLabel l_name = new JLabel("������");
	JTextField name = new JTextField(28);
	JTextArea chatting = new JTextArea(5,42);//�ı������
	
	MyJButton b_sendfile = new MyJButton("��ʼ¼��", 0);
	MyJButton b_send = new MyJButton("��������", 0);
	
	JScrollPane scro_his = new JScrollPane(messageHistory);//���ô�����������Ϣ��ʾ��
	JScrollPane scro_chat = new JScrollPane(chatting);//���ô����ֵĴ�����
	
	public MyPanel()
	{
		JPanel panel = new JPanel();
		
		//�������ؼ����뵽panal��
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
		//������Ϣ��������
		chatting.setBorder(BorderFactory.createLineBorder(Color.gray,1));
		messageHistory.setBorder(BorderFactory.createLineBorder(Color.gray,1));
//		messageHistory.setPreferredSize(new Dimension(465,450));
		messageHistory.setVisible(true);
		messageHistory.setEditable(false);
		chatting.setVisible(true);
		chatting.setEditable(true);
		//������
		b_settings.addActionListener(this);
		b_send.addActionListener(this);
		b_connect.addActionListener(this);
		b_sendfile.addActionListener(this);
		
		//��ʼ�������¼
		try {
			List<Msg> list = SQLiteUtil.queryAll();
			for (final Msg msg : list) {
				String item_source;
				if(msg.getType() == Msg.TYPE_RECEIVED){
					item_source = "    ����    " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.getTime());
					insertDocument(item_source + "\n" + BLANKCONTENT, Color.BLUE);
				}
				else{
					item_source = "    ָ������    " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.getTime());
					insertDocument(item_source + "\n" + BLANKCONTENT, Color.ORANGE);
				}
				switch (msg.getCatagory()) {
				case Msg.CATAGORY_VOICE:
					MyJButton b_play = new MyJButton("������Ϣ", 0, 12);
					b_play.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO ʵ����������
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
					//����ͼƬ
					ImageIcon imageIcon = new ImageIcon(msg.getPath());    // Icon��ͼƬ�ļ��γ�
					Image image = imageIcon.getImage();                         // �����ͼƬ̫���ʺ���Icon
					Image smallImage = image.getScaledInstance(250,300,Image.SCALE_FAST);
					ImageIcon smallIcon = new ImageIcon(smallImage);
					
					messageHistory.setCaretPosition(messageHistory.getDocument().getLength());
					messageHistory.insertIcon(smallIcon);
					break;
				case Msg.CATAGORY_VIDEO:
					MyJButton b_open = new MyJButton("��Ƶ��Ϣ", 0, 12);
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
		//��Information.txt�ļ��еĶ˿��Լ�IP��ַ��Ϣ��������������
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
		//ָ����Ƶ�ļ���/ͼƬ�ļ���
		File directory = new File(".");
		try {
			filePath = directory.getCanonicalPath() + "\\voice";
		} catch (IOException e) {
			e.printStackTrace();
		}
		//���ļ������߳�/�ı������߳�
		socketUtil.receiveFileBySocket(mDestinationIP, mFileDestinationPort, filePath);
		socketUtil.receiveTextByDatagram(mTextLocalPort);
	}
	
	/**
	 * �����¼��������һ����Ϣ
	 * @param msg
	 */
	public static void updateMessageHistory(final Msg msg){
		String item_source;
		if(msg.getType() == Msg.TYPE_RECEIVED){
			item_source = "    ����    " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.getTime());
			insertDocument(item_source + "\n" + BLANKCONTENT, Color.BLUE);
		}
		else{
			item_source = "    ָ������    " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.getTime());
			insertDocument(item_source + "\n" + BLANKCONTENT, Color.ORANGE);
		}
		switch (msg.getCatagory()) {
		case Msg.CATAGORY_VOICE:
			MyJButton b_play = new MyJButton("������Ϣ", 0, 12);
			b_play.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO ʵ����������
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
			//����ͼƬ
			ImageIcon imageIcon = new ImageIcon(msg.getPath());    // Icon��ͼƬ�ļ��γ�
			Image image = imageIcon.getImage();                         // �����ͼƬ̫���ʺ���Icon
			Image smallImage = image.getScaledInstance(250,300,Image.SCALE_FAST);
			ImageIcon smallIcon = new ImageIcon(smallImage);
			
			messageHistory.setCaretPosition(messageHistory.getDocument().getLength());
			messageHistory.insertIcon(smallIcon);
			break;
		case Msg.CATAGORY_VIDEO:
			MyJButton b_open = new MyJButton("��Ƶ��Ϣ", 0, 12);
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
	
	// ���ݴ������ɫ�����֣������ֲ����ı���
	public static void insertDocument(String text, Color textColor)
	{
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, textColor);// ����������ɫ
		StyleConstants.setFontSize(set, 12);// ���������С
		Document doc = messageHistory.getStyledDocument();
		
		try {
			doc.insertString(doc.getLength(), text, set);// ��������
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) 
	{
		//������������
		if(e.getSource() == b_settings)
		{
			MyDialog dialog = new MyDialog();
		}
		//����������ʼ¼��
		if(e.getSource() == b_sendfile)
		{
			if(b_sendfile.getText().equals("��ʼ¼��")){
				mediaUtil.startRecord();
				b_sendfile.setText("����¼��");
			}else if(b_sendfile.getText().equals("����¼��")){
				mediaUtil.stopRecord();
				b_sendfile.setText("��������");
			}else{
				String savePath = new StringBuilder(filePath).append("\\").append(UUID.randomUUID()).append(".wav").toString();
				mediaUtil.saveToFile(savePath, AudioFileFormat.Type.WAVE);
				socketUtil.sendFileBySocket(mFileLocalPort, savePath);
				b_sendfile.setText("��ʼ¼��");
			}
		}
		//�������������ı�
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
