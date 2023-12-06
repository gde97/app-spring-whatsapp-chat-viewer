package mia.appspring.repository;

import mia.appspring.model.Chat;
import mia.appspring.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IChatRepository extends CrudRepository<Chat, Integer> {

    public Chat getByIdchat(Long number);

    public Chat getByName(String name);

    public List<Chat> getChatsByUser(User user);

    public Optional<Chat> getByUserAndName(User user, String name);
}
