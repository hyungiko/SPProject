package jordan.spproject.helper;

/**
 * Created by hyungiko on 8/31/18.
 */

public class DataModel {

    String chattingId;
    String lastContent;
    String lastTime;

    public DataModel(String chattingId, String lastContent, String lastTime) {
        this.chattingId=chattingId;
        this.lastContent=lastContent;
        this.lastTime=lastTime;

    }

    public String getChattingId() {
        return chattingId;
    }

    public String getLastContent() {
        return lastContent;
    }

    public String getLastTime() {
        return lastTime;
    }
}

