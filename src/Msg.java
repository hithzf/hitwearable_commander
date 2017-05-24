

/**
 * 消息实体类
 * Created by hzf on 2017/5/13.
 */

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;

    private  int id;
    private String path;
    private long time;
    private int type;

    /**
     * 
     * @param path
     * @param type
     * @param time
     */
    public Msg(String path, int type, long time){
        this.path = path;
        this.type = type;
        this.time = time;
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
}