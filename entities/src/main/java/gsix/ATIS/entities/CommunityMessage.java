package gsix.ATIS.entities;

import javax.persistence.*;
import java.io.Serializable;
@Entity
@Table(name="community_message")
public class CommunityMessage implements Serializable {
    @Id
    private int message_id;
    @Column(name ="sender_id")
    private String sender_id;

    @Column(name = "receiver_id")
    private String receiver_id;

    @Column(name = "content")
    private String content;

    public int getMessage_id() {
        return message_id;

    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CommunityMessage{" +
                "message_id=" + message_id +
                ", sender_id='" + sender_id + '\'' +
                ", receiver_id='" + receiver_id + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}