package jordan.spproject.helper;

/**
 * Created by hyungiko on 8/31/18.
 */

public class DataModelChatting {

    String chattingId;
    String lastContent;
    String lastTime;

    public DataModelChatting(String chattingId, String lastContent, String lastTime) {
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

