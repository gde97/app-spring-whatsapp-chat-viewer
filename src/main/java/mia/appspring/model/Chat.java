package mia.appspring.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name="chats",  uniqueConstraints={
        @UniqueConstraint(columnNames={"user_id", "idChat"})
})
public class Chat {

    /*
    https://www.baeldung.com/hibernate-one-to-many
    https://www.baeldung.com/jpa-joincolumn-vs-mappedby
    https://www.baeldung.com/jpa-join-column
    https://stackoverflow.com/questions/13370221/persistentobjectexception-detached-entity-passed-to-persist-thrown-by-jpa-and-h
    https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion
    https://stackoverflow.com/questions/49668298/spring-data-jpa-bidirectional-relation-with-infinite-recursion
    https://stackoverflow.com/questions/54874743/spring-data-jpa-infinite-recursion
    https://stackoverflow.com/questions/59950512/could-not-write-json-infinite-recursion
     */

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonManagedReference(value = "chats-user")
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idchat;

    @Column(columnDefinition = "TEXT")
    private String name;

    private String nameowner;

    @OneToMany(fetch = FetchType.LAZY)
    @JsonBackReference(value = "messages-idmessage")
    private List<Message> messages;

    public Chat(){
        this.messages = new ArrayList<>();
    }

    public Chat(User user, String name){
        this.user = user;
        this.name = name;
        this.messages = new ArrayList<>();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getIdchat() {
        return idchat;
    }

    public void setIdchat(Long idChat) {
        this.idchat = idChat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getNameowner() {
        return Arrays.asList(nameowner.split(";"));
    }

    public void setNameowner(List<String> nameowner) {
        String listOwner = "";
        for (String s : nameowner) {
            listOwner = listOwner.concat(s+";");
        }
        listOwner = listOwner.substring(0,listOwner.length()-1);
        this.nameowner = listOwner;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message newMessage) {
        this.messages.add(newMessage);
    }

    public void rmMessage(Message rmMessage) {
        this.messages.remove(rmMessage);
    }
}