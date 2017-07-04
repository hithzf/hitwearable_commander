import java.awt.event.ActionListener;
import java.io.*;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * 媒体（音视频）工具类
 * @author hzf
 *
 */
public class MediaUtil implements ControlContext {
	String errStr;

	AudioInputStream audioInputStream;

	Capture capture = new Capture();

	MyJButton captB;

	File file;

	String fileName;

	/**
	 * 播放音频方法
	 * @param filePath 文件绝对路径
	 */
	public static void playAudio(final String filePath) {
		new Thread() {
			public void run() {
				try {
					AudioInputStream ais = AudioSystem.getAudioInputStream(new File(filePath));
					AudioFormat aif = ais.getFormat();
					SourceDataLine sdl = null;
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, aif);
					sdl = (SourceDataLine) AudioSystem.getLine(info);
					sdl.open(aif);
					sdl.start();

					// play
					int nByte = 0;
					byte[] buffer = new byte[128];
					while (nByte != -1) {
						nByte = ais.read(buffer, 0, 128);
						if (nByte >= 0) {
							int oByte = sdl.write(buffer, 0, nByte);
							// System.out.println(oByte);
						}
					}
					sdl.stop();
				} catch (UnsupportedAudioFileException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自动产生 catch 区块
					e.printStackTrace();
				} catch (LineUnavailableException e) {
					// TODO 自动产生 catch 区块
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	//打开本地播放器并播放视频  
    public static void openVideo(String file) {  
        Runtime rn = Runtime.getRuntime();  
        Process p = null;  
        try {  
            p = rn.exec("D:/Program Files (x86)/Thunder Network/XMP/V5.2.10.5496/Bin/XMP.exe "+file);  
        } catch (Exception e) {  
            System.out.println("Error exec!");
        }  
    }
    
	//打开迅雷播放AMR音频  
    public static void openAMR(String file) {  
        Runtime rn = Runtime.getRuntime();  
        Process p = null;  
        try {  
            p = rn.exec("D:/Program Files (x86)/Thunder Network/XMP/V5.2.10.5496/Bin/XMP.exe "+file);
        } catch (Exception e) {  
            System.out.println("Error exec!");
        }  
    }

	public static void main(String[] args) {
		
	}

	public void open() {
	}

	public void close() {
		if (capture.thread != null) {
			captB.doClick(0);
		}
	}

	public void startRecord() {
		file = null;
		capture.start();
	}

	public void stopRecord() {
		capture.stop();
	}

	/**
	 * 捕获音频类
	 * @author hzf
	 *
	 */
	class Capture implements Runnable {

		TargetDataLine line;
		Thread thread;

		public void start() {
			errStr = null;
			thread = new Thread(this);
			thread.setName("Capture");
			thread.start();
		}

		public void stop() {
			thread = null;
		}

		//音频录制失败
		private void shutDown(String message) {
			if ((errStr = message) != null && thread != null) {
				thread = null;
				captB.setText("开始录音");
				System.err.println(errStr);
			}
		}

		public void run() {

			audioInputStream = null;

			// define the required attributes for our line,
			// and make sure a compatible line is supported.

			AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;// linear
																			// signed
			float rate = Float.valueOf(44100).floatValue();// 44100
			int sampleSize = Integer.valueOf(16).intValue();// 16
			int channels = 2;// stereo
			boolean bigEndian = true;// big endian
			AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels,
					rate, bigEndian);
			;
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

			if (!AudioSystem.isLineSupported(info)) {
				shutDown("Line matching " + info + " not supported.");
				return;
			}

			// get and open the target data line for capture.

			try {
				line = (TargetDataLine) AudioSystem.getLine(info);
				line.open(format, line.getBufferSize());
			} catch (LineUnavailableException ex) {
				shutDown("Unable to open the line: " + ex);
				return;
			} catch (SecurityException ex) {
				shutDown(ex.toString());
				return;
			} catch (Exception ex) {
				shutDown(ex.toString());
				return;
			}

			// play back the captured audio data
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int frameSizeInBytes = format.getFrameSize();
			int bufferLengthInFrames = line.getBufferSize() / 8;
			int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
			byte[] data = new byte[bufferLengthInBytes];
			int numBytesRead;

			line.start();

			while (thread != null) {
				if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
					break;
				}
				out.write(data, 0, numBytesRead);
			}

			// we reached the end of the stream. stop and close the line.
			line.stop();
			line.close();
			line = null;

			// stop and close the output stream
			try {
				out.flush();
				out.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			// load bytes into the audio input stream for playback

			byte audioBytes[] = out.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
			audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);

			try {
				audioInputStream.reset();
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}

		}
	} // End class Capture

	/**
	 * 音频文件保存
	 * @param name 文件名
	 * @param fileType 文件类型
	 */
	public void saveToFile(String name, AudioFileFormat.Type fileType) {

		if (audioInputStream == null) {
			System.err.println("No loaded audio to save");
			return;
		} else if (file != null) {
			createAudioInputStream(file);
		}

		// reset to the beginning of the captured data
		try {
			audioInputStream.reset();
		} catch (Exception e) {
			System.err.println("Unable to reset stream " + e);
			return;
		}

		File file = new File(fileName = name);
		try {
			if (AudioSystem.write(audioInputStream, fileType, file) == -1) {
				throw new IOException("Problems writing to file");
			}
		} catch (Exception ex) {
			System.err.println(ex.toString());
		}
	}

	public void createAudioInputStream(File file) {
		if (file != null && file.isFile()) {
			try {
				this.file = file;
				errStr = null;
				audioInputStream = AudioSystem.getAudioInputStream(file);
				fileName = file.getName();
			} catch (Exception ex) {
				System.err.println(ex.toString());
			}
		} else {
			System.err.println("Audio file required.");
		}
	}
}
