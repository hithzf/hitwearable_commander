import java.awt.BorderLayout;

import javax.swing.JFrame;

public class MyFrame extends JFrame {
	public MyFrame()
	{
	    setTitle("指挥系统");//设置名称
	    setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);//设置大小
	    setResizable(false);
	    setLocationRelativeTo(null);
	    
	    MyPanel mPanel = new MyPanel();
	    add(mPanel);
	}
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 680;
}
