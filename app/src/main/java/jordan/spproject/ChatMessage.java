package jordan.spproject;

import java.util.Date;

/**
 * Created by hyungiko on 8/7/18.
 */

public class ChatMessage {

    public String messageText;
    public String messageUser;
    public String test;
    public long messageTime;

    public ChatMessage(String messageText, String messageUser, String messageEmail, String test2) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.test = messageEmail;
        test2 = "hihi";
        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }


    public String getEmail() {
        return test;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}