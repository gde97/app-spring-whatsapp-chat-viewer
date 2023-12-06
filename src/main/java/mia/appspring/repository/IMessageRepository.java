package mia.appspring.repository;

import mia.appspring.model.Chat;
import mia.appspring.model.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMessageRepository extends CrudRepository<Message, Integer> {

    //public Iterable<MessageProjectDateSenTxtAtt> getByChat(Chat chat);

}

