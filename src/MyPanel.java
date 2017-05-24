import java.awt.Color;
import java.awt.Dimension;
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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
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

	SocketUtil socketUtil = new SocketUtil();
	
	int mTextLocalPort;//���ص��ı����յĶ˿ں�
	int mTextDestinationPort ;//Ŀ��˵��ı����յĶ˿ں�
	
	String mDestinationIP ;//Ŀ��˵�IP��ַ
	
	int mFileLocalPort ;//���ص��ļ����͵Ķ˿ں�
	int mFileDestinationPort ;//Ŀ��˵��ļ����͵Ķ˿ں�

	//��������ؼ�
	MyJButton b_settings = new MyJButton("����", 0);
	MyJButton connect = new MyJButton("����", 0);
	
	static JTextPane messageHistory = new JTextPane();//��Ϣ��¼
	JLabel l_name = new JLabel("������");
	JTextField name = new JTextField(28);
	JTextArea chatting = new JTextArea(5,42);//�ı������
	
	MyJButton b_sendfile = new MyJButton("�����ļ�", 0);
	MyJButton b_send = new MyJButton("��������", 0);
	
	JScrollPane scro_his = new JScrollPane(messageHistory);//���ô�����������Ϣ��ʾ��
	JScrollPane scro_chat = new JScrollPane(chatting);//���ô����ֵĴ�����
	
	public MyPanel()
	{
		JPanel panel = new JPanel();
		
		//�������ؼ����뵽panal��
		b_settings.setPreferredSize(new Dimension(70, 35));
		add(b_settings);
		connect.setPreferredSize(new Dimension(70, 35));
		add(connect);
		
		add(scro_his);
		add(scro_chat);
		
		b_sendfile.setPreferredSize(new Dimension(80, 40));
		add(b_sendfile);
		b_send.setPreferredSize(new Dimension(80, 40));
		add(b_send);
		
		add(panel);
		//������Ϣ��������
		chatting.setBorder(BorderFactory.createLineBorder(Color.gray,1));
		messageHistory.setBorder(BorderFactory.createLineBorder(Color.gray,1));
		messageHistory.setPreferredSize(new Dimension(465,450));
		messageHistory.setVisible(true);
		messageHistory.setEditable(false);
//		messageHistory.setContentType("text/html");
//		messageHistory.setText("<html><body><a href=http://www.baidu.com>baidu</a></body></html>");
		chatting.setVisible(true);
		chatting.setEditable(true);
		//������
		b_settings.addActionListener(this);
		b_send.addActionListener(this);
		connect.addActionListener(this);
		b_sendfile.addActionListener(this);
		
		//��ʼ�������¼
		try {
			List<Msg> list = SQLiteUtil.queryAll();
			for (final Msg msg : list) {
				String item_source;
				if(msg.getType() == Msg.TYPE_RECEIVED)
					item_source = "    ����    " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.getTime());
				else
					item_source = "    ָ������    " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.getTime());
				insertDocument(item_source + "\n" + BLANKCONTENT, Color.BLUE);
				MyJButton b_play = new MyJButton("������Ϣ", 0, 12);
				b_play.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO ʵ����������
						System.out.println(msg.getPath());
						MediaUtil.playAudio(msg.getPath());
					}
				});
				messageHistory.setCaretPosition(messageHistory.getDocument().getLength());
				messageHistory.insertComponent(b_play);
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
		//ָ����Ƶ���λ��
		File directory = new File("."); 
		String filePath = null;
		try {
			filePath = directory.getCanonicalPath() + "\\voice";
		} catch (IOException e) {
			e.printStackTrace();
		}
		//����ͼƬ
//		ImageIcon imageIcon = new ImageIcon(filePath + "\\Chrysanthemum.jpg");
//		messageHistory.insertIcon(imageIcon);
		//���ļ������߳�
//		socketUtil.receiveFileBySocket(mDestinationIP, mFileDestinationPort, filePath);
	}
	
	/**
	 * ����һ����Ϣ
	 * @param msg
	 */
	public static void updateMessageHistory(final Msg msg){
		String item_source;
		if(msg.getType() == Msg.TYPE_RECEIVED)
			item_source = "    ����    " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.getTime());
		else
			item_source = "    ָ������    " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.getTime());
		insertDocument(item_source + "\n" + BLANKCONTENT, Color.BLUE);
		MyJButton b_play = new MyJButton("������Ϣ", 0, 12);
		b_play.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO ʵ����������
				System.out.println(msg.getPath());
				MediaUtil.playAudio(msg.getPath());
			}
		});
		messageHistory.setCaretPosition(messageHistory.getDocument().getLength());
		messageHistory.insertComponent(b_play);
		insertDocument("\n", Color.BLACK);
	}
	
	public static void insertDocument(String text, Color textColor)// ���ݴ������ɫ�����֣������ֲ����ı���
	{
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, textColor);// ����������ɫ
		StyleConstants.setFontSize(set, 12);// ���������С
		Document doc = messageHistory.getStyledDocument();
		
		try {
			doc.insertString(doc.getLength(), text, set);// ��������
		} catch (BadLocationException e) {
		}
	}

	public void actionPerformed(ActionEvent e) 
	{

		//������������
		if(e.getSource() == b_settings)
		{
			MyDialog dialog = new MyDialog();
		}
		//�������������ļ�
		if(e.getSource() == b_sendfile)
		{
			final JFileChooser fc = new JFileChooser();
			if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog(this))
			{
				socketUtil.sendFileBySocket(mFileLocalPort, fc.getSelectedFile().getPath());
			}
		}
		//�������������ļ�
//		if(e.getSource()== receivefile)
//		{
//			final JFileChooser fc = new JFileChooser();
//			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//			if(JFileChooser.APPROVE_OPTION == fc.showSaveDialog(this))
//			{				
//				System.out.println(fc.getSelectedFile().getPath());
//				socketUtil.receiveFileBySocket(mDestinationIP, mFileDestinationPort, fc.getSelectedFile().getPath());
//			}
//		}
		//�����������ӹ���
		if(e.getSource()==connect)
		{
			new Thread() 
			{
				public void run() 
				{
					while(true)
					{
						try
						{
							DatagramSocket ds = new DatagramSocket(mTextLocalPort);
							byte[] buf = new byte[1024];
							DatagramPacket dp = new DatagramPacket(buf,buf.length);
							ds.receive(dp);
							String messageget = "<����>"+new String(dp.getData(),0,dp.getLength()) + " from " + dp.getAddress() + ":" + dp.getPort();
							insertDocument(messageget+"\n", Color.BLACK);
							System.out.println(messageget);
							ds.close();
						}
						catch(IOException ee)
						{
							
						}
					}
				}
			}.start();
		}
		//�������������ı�
		if(e.getSource() == b_send)
		{
			try
			{
				String messagesent = chatting.getText();
				DatagramSocket ds = new DatagramSocket();
				DatagramPacket dp = new DatagramPacket(messagesent.getBytes(),messagesent.getBytes().length,
																								InetAddress.getByName(mDestinationIP),mTextDestinationPort);
				ds.send(dp);
				ds.close();
				insertDocument("<ָ������>"+chatting.getText()+" from " + dp.getAddress() + ":" + dp.getPort()+"\n", Color.BLACK);
				chatting.setText("");
			}
			catch(Exception se)
			{
				se.printStackTrace();
			}
		}
	}

}
