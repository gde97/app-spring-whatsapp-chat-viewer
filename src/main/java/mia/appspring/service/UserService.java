package mia.appspring.service;

import jakarta.transaction.Transactional;
import mia.appspring.model.Chat;
import mia.appspring.model.User;
import mia.appspring.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    /*
    https://github.com/codeforgeyt/one-to-many-web-service/tree/main
     */

    private final IUserRepository userRepository;

    @Autowired
    public UserService(IUserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User newUser(String email, String password, String phone){
        return new User(email, password, phone);
    }

    public User saveUser(User newUser){
        return userRepository.save(newUser);
    }

    public User getUser(Long idUser){
        return userRepository.getUserById(idUser);
    }

    public Optional<User> getUserByEmail(String email){
        return userRepository.getUserByEmail(email);
    }

    public User register(String email, String password, String phone){
        Optional<User> result = getUserByEmail(email);
        if (result.isPresent()){
            return null;
        }
        User user = new User(email, password, phone);
        saveUser(user);
        return user;
    }

    public Optional<User> login(String email, String password){
        Optional<User> result = getUserByEmail(email);
        if (result.isPresent()) {
            if (password.matches(result.get().getPassword())){
                return result;
            }
        }
        return Optional.empty();
    }

    @Transactional
    public User updateEmail(Long idUser, String email){
        Optional<User> result = getUserByEmail(email);
        if (result.isPresent()){
            return null;
        }
        User user = getUser(idUser);
        user.setEmail(email);
        //userRepository.updateUserEmail(idUser, email);
        return user;
    }

    @Transactional
    public User updatePass(Long idUser, String pass){
        User user = getUser(idUser);
        user.setPassword(pass);
        return user;
    }

    @Transactional
    public User updatePhone(Long idUser, String phone){
        User user = getUser(idUser);
        user.setPhone(phone);
        return user;
    }

    public List<Chat> getChats(Long idUser){
        return getUser(idUser).getChats();
    }

    @Transactional
    public User addChat(Long idUser, Chat addChat){
        User user = getUser(idUser);
        user.addChat(addChat);
        return user;
    }

    @Transactional
    public User removeChat(Long idUser, Chat rmChat){
        User user = getUser(idUser);
        user.removeChat(rmChat);
        return user;
    }
}
