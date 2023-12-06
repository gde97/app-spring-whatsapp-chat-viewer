package mia.appspring.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name="messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonManagedReference(value = "messages-idmessage")
    private Long idmessage;

    private String datetime;

    @Column(columnDefinition = "TEXT")
    private String sender;

    @Column(columnDefinition = "TEXT")
    private String text;

    private String nameattachment;

    private Boolean attachment;

    public Message(){

    }

    public Message(String dateTime, String sender, String text, Boolean attachment){
        this.datetime = dateTime;
        this.sender = sender;
        this.text = text;
        this.attachment = attachment;
    }

    public Long getIdmessage() {
        return idmessage;
    }

    public void setIdmessage(Long message) {
        this.idmessage = message;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String dateTime) {
        this.datetime = dateTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNameattachment() {
        return nameattachment;
    }

    public void setNameattachment(String nameAttachment) {
        this.nameattachment = nameAttachment;
    }

    public Boolean getAttachment() {
        return attachment;
    }

    public void setAttachment(Boolean attachment) {
        this.attachment = attachment;
    }
}