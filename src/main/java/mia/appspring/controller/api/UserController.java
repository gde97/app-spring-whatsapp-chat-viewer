package mia.appspring.controller.api;

import mia.appspring.model.Chat;
import mia.appspring.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import mia.appspring.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * get the user data by id
     * @param idUser number of user
     * @return the user
     */
    // ?id=1
    @GetMapping(params = {"id"})
    public ResponseEntity<User> getById(@RequestParam("id") Long idUser) {
        return ResponseEntity.ok().body(userService.getUser(idUser));
    }

    /**
     * to register a new user
     * it checks if exist another with same email
     * @param payload contain a json with email and password
     * @return 400 if exists, 200 if created
     */
    // /new
    // body params: email, password
    @PostMapping(value = {"/new"})
    public ResponseEntity<String> insertNewUser(@RequestBody Map<String, String> payload){
        if (!payload.containsKey("email") && !payload.containsKey("password")){
            return ResponseEntity.badRequest().build();
        }
        if (userService.register(payload.get("email"), payload.get("password"), "") == null){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * login of user, allow to know the id to get the right chat
     * @param body contain a json with email and password
     * @return 200 with the user if correct, else 400 with nothing
     */
    // /login
    // body params: email, password
    @PostMapping(value = {"/login"})
    public ResponseEntity<Optional<User>> login(@RequestBody Map<String, String> body){
        if (!body.containsKey("email") && !body.containsKey("password")){
            return ResponseEntity.badRequest().body(Optional.empty());
        }
        Optional<User> result = userService.login(body.get("email"), body.get("password"));
        if (result.isPresent()) {
            return ResponseEntity.ok().body(result);
        }
        return ResponseEntity.badRequest()
                .body(Optional.empty());
    }

    /**
     * to change the email
     * @param idUser user id
     * @param email user new email
     * @return 200 if the email has not been used yet, else 400
     */
    // /update/email
    // body params: id, email
    @PatchMapping(value = {"/update/email"})
    public ResponseEntity<String> updateEmail(@RequestParam("id") Long idUser,
                                                @RequestParam("email") String email){
        if (userService.updateEmail(idUser, email) == null){
            return ResponseEntity.badRequest()
                    .body("user with this email already exist, change email");
        }
        return ResponseEntity.ok()
                .body("user update email");
    }

    /**
     * to change password
     * @param idUser user id
     * @param password user new password
     * @return always 200
     */
    // /update/pass
    // body params: id, pass
    @PatchMapping(value = {"/update/pass"})
    public ResponseEntity<String> updatePass(@RequestParam("id") Long idUser,
                                              @RequestParam("pass") String password){
        userService.updatePass(idUser, password);
        return ResponseEntity.ok()
                .body("user update password");
    }

    /**
     * to change phone
     * @param idUser user id
     * @param phone user new phone
     * @return always 200
     */
    // /update/phone
    // body params: id, phone
    @PatchMapping(value = {"/update/phone"})
    public ResponseEntity<String> updatePhone(@RequestParam("id") Long idUser,
                                             @RequestParam("phone") String phone){
        userService.updatePhone(idUser, phone);
        return ResponseEntity.ok()
                .body("user update phone");
    }

    /**
     * retrieve all the chat of a given iduser
     * @param idUser identification of user
     * @return list of chat of the user
     */
    // /chats?id=1
    @GetMapping(value = {"/chats"}, params = {"id"})
    public List<Chat> getAllChatOfUser(@RequestParam("id") Long idUser) {
        return userService.getChats(idUser);
    }
}