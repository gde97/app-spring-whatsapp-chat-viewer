package mia.appspring.controller.api;

import mia.appspring.model.Chat;
import mia.appspring.repository.MessageProjectDateSenTxtAtt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import mia.appspring.repository.IMessageRepository;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping("/api/message")
public class MessageController {

    private final IMessageRepository messagesRepository;

    @Autowired
    public MessageController(IMessageRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }



}
