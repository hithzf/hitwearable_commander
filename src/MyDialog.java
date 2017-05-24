import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class MyDialog extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// JLable
	JLabel l_text_local_port = new JLabel("本地的文本接收的端口号：");
	JLabel l_text_des_port = new JLabel("目标端的文本接收端口号：");
	JLabel l_des_ip = new JLabel("              目标端的IP地址：");
	JLabel l_file_local_port = new JLabel("本地的文件发送的端口号：");
	JLabel l_file_des_port = new JLabel("目标端的文件发送端口号：");
	// JTextField
	JTextField text_des_port = new JTextField(10);
	JTextField text_local_port = new JTextField(10);
	JTextField des_ip = new JTextField(10);
	JTextField file_local_port = new JTextField(10);
	JTextField file_des_port = new JTextField(10);
	// JButton
	MyJButton b_save = new MyJButton("确定", 0);
	MyJButton b_cancel = new MyJButton("取消", 0);

	JDialog dialog = new JDialog();
	JPanel panel = new JPanel();

	// 构造函数
	public MyDialog() {
		// Button监听器
		b_save.addActionListener(this);
		b_cancel.addActionListener(this);
		// 加入JLable和JTextField
		l_text_local_port.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		l_text_des_port.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		l_des_ip.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		l_file_local_port.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		l_file_des_port.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		panel.add(l_text_local_port);
		panel.add(text_local_port);
		panel.add(l_text_des_port);
		panel.add(text_des_port);
		panel.add(l_des_ip);
		panel.add(des_ip);
		panel.add(l_file_local_port);
		panel.add(file_local_port);
		panel.add(l_file_des_port);
		panel.add(file_des_port);
		// Dialog的一些设置
		dialog.setTitle("设置");
		dialog.setLocation(400, 20);
		dialog.add(panel);
		dialog.setSize(350, 220);
		dialog.setVisible(true);
		// 加入JButton
		b_save.setPreferredSize(new Dimension(70, 35));
		panel.add(b_save);
		b_save.setVisible(true);
		b_cancel.setPreferredSize(new Dimension(70, 35));
		panel.add(b_cancel);
		b_cancel.setVisible(true);
		try {
			// 将文件中存储的端口信息显示到TextField中
			BufferedReader in = new BufferedReader(new FileReader("Informatin.txt"));
			text_local_port.setText(in.readLine());
			text_des_port.setText(in.readLine());
			des_ip.setText(in.readLine());
			file_local_port.setText(in.readLine());
			file_des_port.setText(in.readLine());
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent action) {
		// 监听器：设置各个端口以及IP地质
		if (action.getSource() == b_save) {
			String s = new String();// 用于暂时储存从文件中读取出来的字符串
			String infor = new StringBuilder(text_local_port.getText()).append("\n").append(text_des_port.getText()).append("\n")
					.append(des_ip.getText()).append("\n").append(file_local_port.getText()).append("\n")
					.append(file_des_port.getText()).toString();
			try {
				BufferedReader br = new BufferedReader(new StringReader(infor));
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("Informatin.txt")));
				while ((s = br.readLine()) != null) {
					pw.println(s);
				}
				pw.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			dialog.setVisible(false);
		}
		// 监听器：取消
		if (action.getSource() == b_cancel) {
			dialog.setVisible(false);
		}
	}
}
