package mia.appspring.service;

import mia.appspring.model.Chat;
import mia.appspring.model.Message;
import mia.appspring.repository.IMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final IMessageRepository messageRepository;

    @Autowired
    public MessageService(IMessageRepository messageRepository){
        this.messageRepository = messageRepository;
    }

    public Message newMessage(){
        return new Message();
    }

    public Message newMessage(String dateTime, String sender, String text, Boolean attachment){
        return new Message(dateTime, sender, text, attachment);
    }

    public Iterable<Message> saveAllMessage(List<Message> newList){
        return messageRepository.saveAll(newList);
    }

}
