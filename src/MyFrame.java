import java.awt.BorderLayout;

import javax.swing.JFrame;

public class MyFrame extends JFrame {
	public MyFrame()
	{
	    setTitle("ָ��ϵͳ");//��������
	    setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);//���ô�С
	    setResizable(false);
	    setLocationRelativeTo(null);
	    
	    MyPanel mPanel = new MyPanel();
	    add(mPanel);
	}
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 680;
}
