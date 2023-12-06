package mia.appspring.service;

import jakarta.transaction.Transactional;
import mia.appspring.model.Chat;
import mia.appspring.model.Message;
import mia.appspring.model.User;
import mia.appspring.repository.IChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final IChatRepository chatRepository;

    @Autowired
    public ChatService(IChatRepository chatRepository){
        this.chatRepository = chatRepository;
    }

    public Chat newChat(User user, String name){
        return new Chat(user, name);
    }

    public Chat saveChat(Chat newChat) {
        return chatRepository.save(newChat);
    }

    public Chat getChat(Long idChat){
        return chatRepository.getByIdchat(idChat);
    }

    public Optional<Chat> getChatByUserName(User user, String name){
        return chatRepository.getByUserAndName(user, name);
    }

    public Chat getChatWithLastMessage(Long idChat){
        Chat nuova = chatRepository.getByIdchat(idChat);
        //nuova.getMessages().getLast();
        return nuova;
    }

    @Transactional
    public Chat updateNameChat(Long idChat, String newNameChat){
        Chat chat = getChat(idChat);
        List<Chat> list = chatRepository.getChatsByUser(chat.getUser());
        for (Chat element : list) {
            if (element.getName().matches(newNameChat)){
                return null;
            }
        }
        chat.setName(newNameChat);
        return chat;
    }

    @Transactional
    public Chat updateNameOwner(Long idChat, List<String> nameOwner){
        Chat chat = getChat(idChat);
        chat.setNameowner(nameOwner);
        return chat;
    }

    public List<Message> getMessages(Long idChat){
        Chat chat = getChat(idChat);
        if (chat == null){
            return new ArrayList<>();
        }
        return chat.getMessages();
    }

    @Transactional
    public Chat addAMessage(Long id_chat, Message addMsg){
        Chat chat = getChat(id_chat);
        chat.addMessage(addMsg);
        return chat;
    }

    @Transactional
    public Chat addAllMessage(Long id_chat, List<Message> addListMsg){
        Chat chat = getChat(id_chat);
        chat.setMessages(addListMsg);
        return chat;
    }

    @Transactional
    public Chat removeAMessage(Long id_chat, Message rmMsg){
        Chat chat = getChat(id_chat);
        chat.rmMessage(rmMsg);
        return chat;
    }
}
