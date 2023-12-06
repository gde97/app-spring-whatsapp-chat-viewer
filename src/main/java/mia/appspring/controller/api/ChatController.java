package mia.appspring.controller.api;

import mia.appspring.model.Chat;
import mia.appspring.model.Message;
import mia.appspring.model.User;
import mia.appspring.repository.ChatProjectChatName;

import mia.appspring.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import mia.appspring.repository.IChatRepository;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }


    /**
     * retrieve a single chat by its id
     * @param idChat identification of chat
     * @return a chat with identification and name
     */
    // ?id=1
    @GetMapping(params = {"id"})
    public Chat getAChat(@RequestParam("id") Long idChat) {
        return chatService.getChat(idChat);
    }

    /**
     * retrieve all messages of a chat
     * @param idChat identification of chat
     * @return list of messages
     */
    // /messages?id=1
    @GetMapping(value = "/messages", params = {"id"})
    public List<Message> getAllMessages(@RequestParam("id") Long idChat){
        return chatService.getMessages(idChat);
    }

    /**
     * update the name of chat
     * @param idChat identification of chat
     * @param newNameChat the new name
     * @return 200 if is updated, else 400
     */
    // /name?id=1
    @PatchMapping(value = "/name", params = {"id"})
    public ResponseEntity<String> updateNameChat(@RequestParam("id") Long idChat,
                                                 @RequestBody String newNameChat){
        if (chatService.updateNameChat(idChat, newNameChat) == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * update the name of owner chat
     * @param idChat identification of chat
     * @param nameOwner strings of name
     * @return the updated chat
     */
    // /owner?id=1
    @PatchMapping(value = "/owner", params = {"id"})
    public Chat updateNameOwner(@RequestParam("id") Long idChat,
                                @RequestBody List<String> nameOwner){
        return chatService.updateNameOwner(idChat, nameOwner);
    }

}