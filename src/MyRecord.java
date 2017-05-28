import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;

public class MyRecord extends JFrame implements ActionListener{

	//����¼����ʽ
	AudioFormat af = null;
	//����Ŀ��������,���Դ��ж�ȡ��Ƶ����,�� TargetDataLine �ӿ��ṩ��Ŀ�������еĻ�������ȡ���������ݵķ�����
	TargetDataLine td = null;
	//����Դ������,Դ�������ǿ���д�����ݵ������С����䵱���Ƶ����Դ��Ӧ�ó�����Ƶ�ֽ�д��Դ�����У������ɴ����ֽڻ��岢�����Ǵ��ݸ���Ƶ����
	SourceDataLine sd = null;
	//�����ֽ��������������
	ByteArrayInputStream bais = null;
	ByteArrayOutputStream baos = null;
	//������Ƶ������
	AudioInputStream ais = null;
	//����ֹͣ¼���ı�־��������¼���̵߳�����
	Boolean stopflag = false;
	
	
	//��������Ҫ�����
	JPanel jp1,jp2,jp3;
	JLabel jl1=null;
	JButton captureBtn,stopBtn,playBtn,saveBtn;
	public static void main(String[] args) {
		
		//����һ��ʵ��
		MyRecord mr = new MyRecord();

	}
	//���캯��
	public MyRecord()
	{
		//�����ʼ��
		jp1 = new JPanel();
		jp2 = new JPanel();
		jp3 = new JPanel();
		
		//��������
		Font myFont = new Font("������κ",Font.BOLD,30);
		jl1 = new JLabel("¼�������ܵ�ʵ��");
		jl1.setFont(myFont);
		jp1.add(jl1);
		
		captureBtn = new JButton("��ʼ¼��");
		//�Կ�ʼ¼����ť����ע�����
		captureBtn.addActionListener(this);
		captureBtn.setActionCommand("captureBtn");
		//��ֹͣ¼������ע�����
		stopBtn = new JButton("ֹͣ¼��");
		stopBtn.addActionListener(this);
		stopBtn.setActionCommand("stopBtn");
		//�Բ���¼������ע�����
		playBtn = new JButton("����¼��");
		playBtn.addActionListener(this);
		playBtn.setActionCommand("playBtn");
		//�Ա���¼������ע�����
		saveBtn = new JButton("����¼��");
		saveBtn.addActionListener(this);
		saveBtn.setActionCommand("saveBtn");
		
		
		this.add(jp1,BorderLayout.NORTH);
		this.add(jp2,BorderLayout.CENTER);
		this.add(jp3,BorderLayout.SOUTH);
		jp3.setLayout(null);
		jp3.setLayout(new GridLayout(1, 4,10,10));
		jp3.add(captureBtn);
		jp3.add(stopBtn);
		jp3.add(playBtn);
		jp3.add(saveBtn);
		//���ð�ť������
		captureBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);
        saveBtn.setEnabled(false);
		//���ô��ڵ�����
		this.setSize(400,300);
		this.setTitle("¼����");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand().equals("captureBtn"))
		{
			//�����ʼ¼����ť��Ķ���
			//ֹͣ��ť��������
			captureBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            playBtn.setEnabled(false);
            saveBtn.setEnabled(false);
            
            //����¼���ķ���
            capture();
		}else if (e.getActionCommand().equals("stopBtn")) {
			//���ֹͣ¼����ť�Ķ���
			captureBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            playBtn.setEnabled(true);
            saveBtn.setEnabled(true);
            //����ֹͣ¼���ķ���     
            stop();
			
		}else if(e.getActionCommand().equals("playBtn"))
		{
			//���ò���¼���ķ���
			play();
		}else if(e.getActionCommand().equals("saveBtn"))
		{
			//���ñ���¼���ķ���
			save();
		}
		
	}
	//��ʼ¼��
	public void capture()
	{
		try {
			//afΪAudioFormatҲ������Ƶ��ʽ
			af = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class,af);
			td = (TargetDataLine)(AudioSystem.getLine(info));
			//�򿪾���ָ����ʽ���У�������ʹ�л�����������ϵͳ��Դ����ÿɲ�����
			td.open(af);
			//����ĳһ������ִ������ I/O
			td.start();
			
			//��������¼�����߳�
			Record record = new Record();
			Thread t1 = new Thread(record);
			t1.start();
			
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
			return;
		}
	}
	//ֹͣ¼��
	public void stop()
	{
		stopflag = true;			
	}
	//����¼��
	public void play()
	{
		//��baos�е�����ת��Ϊ�ֽ�����
		byte audioData[] = baos.toByteArray();
		//ת��Ϊ������
		bais = new ByteArrayInputStream(audioData);
		af = getAudioFormat();
		ais = new AudioInputStream(bais, af, audioData.length/af.getFrameSize());
		
		try {
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, af);
            sd = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sd.open(af);
            sd.start();
            //�������Ž���
            Play py = new Play();
            Thread t2 = new Thread(py);
            t2.start();           
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				//�ر���
				if(ais != null)
				{
					ais.close();
				}
				if(bais != null)
				{
					bais.close();
				}
				if(baos != null)
				{
					baos.close();
				}
				
			} catch (Exception e) {		
				e.printStackTrace();
			}
		}
		
	}
	//����¼��
	public void save()
	{
		 //ȡ��¼��������
        af = getAudioFormat();

        byte audioData[] = baos.toByteArray();
        bais = new ByteArrayInputStream(audioData);
        ais = new AudioInputStream(bais,af, audioData.length / af.getFrameSize());
        //�������ձ�����ļ���
        File file = null;
        //д���ļ�
        try {	
        	//�Ե�ǰ��ʱ������¼��������
        	//��¼�����ļ���ŵ�F���������ļ�����
        	File filePath = new File("F:/�����ļ�");
        	if(!filePath.exists())
        	{//����ļ������ڣ��򴴽���Ŀ¼
        		filePath.mkdir();
        	}
        	file = new File(filePath.getPath()+"/"+System.currentTimeMillis()+".mp3");      
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	//�ر���
        	try {
        		
        		if(bais != null)
        		{
        			bais.close();
        		} 
        		if(ais != null)
        		{
        			ais.close();		
        		}
			} catch (Exception e) {
				e.printStackTrace();
			}   	
        }
	}
	//����AudioFormat�Ĳ���
	public AudioFormat getAudioFormat() 
	{
		//����ע�Ͳ���������һ����Ƶ��ʽ�����߶�����
		AudioFormat.Encoding encoding = AudioFormat.Encoding.
        PCM_SIGNED ;
		float rate = 8000f;
		int sampleSize = 16;
		String signedString = "signed";
		boolean bigEndian = true;
		int channels = 1;
		return new AudioFormat(encoding, rate, sampleSize, channels,
				(sampleSize / 8) * channels, rate, bigEndian);
//		//��������ÿ�벥�ź�¼�Ƶ�������
//		float sampleRate = 16000.0F;
//		// ������8000,11025,16000,22050,44100
//		//sampleSizeInBits��ʾÿ�����д˸�ʽ�����������е�λ��
//		int sampleSizeInBits = 16;
//		// 8,16
//		int channels = 1;
//		// ������Ϊ1��������Ϊ2
//		boolean signed = true;
//		// true,false
//		boolean bigEndian = true;
//		// true,false
//		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);
	}
	//¼���࣬��ΪҪ�õ�MyRecord���еı��������Խ��������ڲ���
	class Record implements Runnable
	{
		//������¼�����ֽ�����,��Ϊ������
		byte bts[] = new byte[10000];
		//���ֽ������װ��������մ��뵽baos��
		//��дrun����
		public void run() {	
			baos = new ByteArrayOutputStream();		
			try {
				System.out.println("ok3");
				stopflag = false;
				while(stopflag != true)
				{
					//��ֹͣ¼��û����ʱ�����߳�һֱִ��	
					//�������е����뻺������ȡ��Ƶ���ݡ�
					//Ҫ��ȡbts.length���ȵ��ֽ�,cnt ��ʵ�ʶ�ȡ���ֽ���
					int cnt = td.read(bts, 0, bts.length);
					if(cnt > 0)
					{
						baos.write(bts, 0, cnt);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					//�رմ򿪵��ֽ�������
					if(baos != null)
					{
						baos.close();
					}	
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					td.drain();
					td.close();
				}
			}
		}
		
	}
	//������,ͬ��Ҳ�����ڲ���
	class Play implements Runnable
	{
		//����baos�е����ݼ���
		public void run() {
			byte bts[] = new byte[10000];
			try {
				int cnt;
	            //��ȡ���ݵ���������
	            while ((cnt = ais.read(bts, 0, bts.length)) != -1) 
	            {
	                if (cnt > 0) 
	                {
	                    //д�뻺������
	                    //����Ƶ����д�뵽��Ƶ��
	                    sd.write(bts, 0, cnt);
	                }
	            }
	           
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				 sd.drain();
		         sd.close();
			}
			
			
		}		
	}	
}