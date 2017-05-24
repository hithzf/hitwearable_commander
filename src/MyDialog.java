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
	JLabel l_text_local_port = new JLabel("���ص��ı����յĶ˿ںţ�");
	JLabel l_text_des_port = new JLabel("Ŀ��˵��ı����ն˿ںţ�");
	JLabel l_des_ip = new JLabel("              Ŀ��˵�IP��ַ��");
	JLabel l_file_local_port = new JLabel("���ص��ļ����͵Ķ˿ںţ�");
	JLabel l_file_des_port = new JLabel("Ŀ��˵��ļ����Ͷ˿ںţ�");
	// JTextField
	JTextField text_des_port = new JTextField(10);
	JTextField text_local_port = new JTextField(10);
	JTextField des_ip = new JTextField(10);
	JTextField file_local_port = new JTextField(10);
	JTextField file_des_port = new JTextField(10);
	// JButton
	MyJButton b_save = new MyJButton("ȷ��", 0);
	MyJButton b_cancel = new MyJButton("ȡ��", 0);

	JDialog dialog = new JDialog();
	JPanel panel = new JPanel();

	// ���캯��
	public MyDialog() {
		// Button������
		b_save.addActionListener(this);
		b_cancel.addActionListener(this);
		// ����JLable��JTextField
		l_text_local_port.setFont(new Font("΢���ź�", Font.PLAIN, 14));
		l_text_des_port.setFont(new Font("΢���ź�", Font.PLAIN, 14));
		l_des_ip.setFont(new Font("΢���ź�", Font.PLAIN, 14));
		l_file_local_port.setFont(new Font("΢���ź�", Font.PLAIN, 14));
		l_file_des_port.setFont(new Font("΢���ź�", Font.PLAIN, 14));
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
		// Dialog��һЩ����
		dialog.setTitle("����");
		dialog.setLocation(400, 20);
		dialog.add(panel);
		dialog.setSize(350, 220);
		dialog.setVisible(true);
		// ����JButton
		b_save.setPreferredSize(new Dimension(70, 35));
		panel.add(b_save);
		b_save.setVisible(true);
		b_cancel.setPreferredSize(new Dimension(70, 35));
		panel.add(b_cancel);
		b_cancel.setVisible(true);
		try {
			// ���ļ��д洢�Ķ˿���Ϣ��ʾ��TextField��
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
		// �����������ø����˿��Լ�IP����
		if (action.getSource() == b_save) {
			String s = new String();// ������ʱ������ļ��ж�ȡ�������ַ���
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
		// ��������ȡ��
		if (action.getSource() == b_cancel) {
			dialog.setVisible(false);
		}
	}
}
