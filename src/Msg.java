

/**
 * 消息实体类
 * Created by hzf on 2017/5/13.
 */

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    
    public static final int CATAGORY_VOICE = 0;
    public static final int CATAGORY_TEXT = 1;
    public static final int CATAGORY_IMAGE = 2;
    public static final int CATAGORY_VIDEO = 3;

    private  int id;
    private String path;
    private long time;
    private int type;//接收or发送
    private int catagory;//语音or文字or图片or视频

    /**
     * 构造函数
     * @param path
     * @param type
     * @param time
     */
    public Msg(String path, int type, long time){
        this.path = path;
        this.type = type;
        this.time = time;
        this.catagory = CATAGORY_VOICE;
    }
    
    /**
     * 构造函数
     * @param path
     * @param type
     * @param time
     * @param catagory
     */
    public Msg(String path, int type, long time, int catagory){
        this.path = path;
        this.type = type;
        this.time = time;
        this.catagory = catagory;
    }

    public int getId(){
        return this.id;
    }
    public long getTime(){
        return this.time;
    }
    public int getType(){
        return this.type;
    }
    public String getPath(){
        return this.path;
    }
    public int getCatagory(){
    	return this.catagory;
    }
}