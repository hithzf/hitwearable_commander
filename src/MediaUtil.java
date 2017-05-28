import java.io.*;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MediaUtil {
	public static void playAudio(final String filePath){
		new Thread(){
			public void run(){
				try{
							            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(filePath));
							            AudioFormat aif = ais.getFormat();
							            SourceDataLine sdl = null;
							            DataLine.Info info = new DataLine.Info(SourceDataLine.class,aif);
							            sdl = (SourceDataLine)AudioSystem.getLine(info);
							            sdl.open(aif);
							            sdl.start();
							            
							            //play
							            int nByte = 0;
							            byte[] buffer = new byte[128];
							            while(nByte != -1){
							                nByte = ais.read(buffer,0,128);
							                if(nByte >= 0){
							                    int oByte = sdl.write(buffer, 0, nByte);
							                    //System.out.println(oByte);
							                }
							            }
							            sdl.stop();
							        }catch(UnsupportedAudioFileException e){
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
	
	public static void main(String[] args) {
//		playAudio("voice/e2d524e7-9254-4d55-bff4-8453993e08c9.amr");
		playAudio("voice/ALARM.WAV");
	}
}
