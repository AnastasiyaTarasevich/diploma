package com.example.diploma.services;

import com.example.diploma.dto.message.ChatRoomDTO;
import com.example.diploma.dto.message.MessageDTO;
import com.example.diploma.dto.message.UserDTO;
import com.example.diploma.models.*;
import com.example.diploma.repos.ChatRoomRepo;
import com.example.diploma.repos.LogisticsRepo;
import com.example.diploma.repos.MessageRepo;
import com.example.diploma.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final MessageRepo messageRepo;
    private final ChatRoomRepo chatRoomRepo;
    private final LogisticsRepo logisticsRepo;

    public boolean createUser (User user,String role)
    {
        if(userRepo.findByLogin(user.getLogin())!=null) return false;
        user.setActive(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(Status.OFFLINE);
        if(role.equals("user"))
        {
            user.getRoles().add(Roles.USER);
            userRepo.save(user);
        }
        else
        {
            user.getRoles().add(Roles.LOGISTICS);
            Logistic logistic=new Logistic();
            logistic.setUser(user);
            userRepo.save(user);

            logisticsRepo.save(logistic);
        }
        return true;


    }
    public boolean createSupplier (User user)
    {
        if(userRepo.findByLogin(user.getLogin())!=null) return false;
        user.setActive(true);
        user.getRoles().add(Roles.SUPPLIER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(Status.OFFLINE);
        userRepo.save(user);
        return true;
    }
    public void userSavenewRole(User user, String selectedRole) {

        Set<Roles> roles = new HashSet<>();
        roles.add(Roles.valueOf(selectedRole));
        user.setRoles(roles);
        userRepo.save(user);
    }

    public User findById(int id) {
        return userRepo.findById(id).orElse(null);
    }

    public void updateProfile(User user, String password, String email, String username) {
        String userEmail = user.getEmail();
        String Username=user.getUsername();
        boolean isLoginChanged=(username != null && !username.equals(Username)) ||
                (username != null && !username.equals(Username));
        boolean isEmailChanged = (email != null && !email.equals(userEmail)) ||
                (userEmail != null && !userEmail.equals(email));

        if (isEmailChanged) {
            user.setEmail(email);

        }
        if (isLoginChanged) {
            user.setLogin(username);

        }
        if (!StringUtils.isEmpty(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepo.save(user);


    }

    public void updateResetPasswordToken(String token, String email)
    {
        User user=userRepo.findByEmail(email);
        if(user!=null)
        {
            user.setResetPasswordToken(token);
            userRepo.save(user);
        }
        else {
            System.out.println("Такого нет!");/// сделать обработку
        }

    }
    public User getByResetPasswordToken(String token) {
        return userRepo.findByResetPasswordToken(token);
    }

    public void updatePassword(User customer, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        customer.setPassword(encodedPassword);

        customer.setResetPasswordToken(null);
        userRepo.save(customer);
    }


    public UserDTO changeStatusOnline(User userDetails) {
        User user = userRepo.findByLogin(userDetails.getLogin());
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin(user.getLogin());

        userDTO.setRole(user.getRoles().toString());
        userDTO.setName(user.getName());
        userDTO.setSurname(user.getSurname());

        List<Message> chatMessage = messageRepo.findAllBySenderIdAndStatus(user, MessageStatus.DELIVERED);
        Set<ChatRoom> chatRoomId = new HashSet<>();
        for(Message chatMsg: chatMessage) {
            chatRoomId.add(chatMsg.getChatRoom());
        }
        for (ChatRoom chatRoom : chatRoomId){
            ChatRoom chatRoomDB = chatRoomRepo.findById(chatRoom.getIdChatRoom()).orElse(null);
            if (chatRoomDB!=null) {
                ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
                chatRoomDTO.setIdChatRoom(chatRoomDB.getIdChatRoom());
                chatRoomDTO.setLoginUserSender(chatRoomDB.getSender().getLogin());
                chatRoomDTO.setLoginUserRecipient(chatRoomDB.getRecipient().getLogin());
                List<Message> chatMessageDB = messageRepo.findByChatRoom(chatRoomDB);
                if (chatMessageDB!=null){
                    for (Message msg : chatMessageDB) {
                        chatRoomDTO.setMessageDTO(new MessageDTO(chatRoom.getIdChatRoom(),
                                chatRoomDTO.getLoginUserSender(),
                                msg.getSenderId().getSurname() + " " + msg.getSenderId().getName(),
                                msg.getContent(), msg.getTimestamp(),
                                msg.getStatus().toString()));
                    }
                }
                userDTO.setChatRoomDTOS(chatRoomDTO);
            }
        }
        user.setStatus(Status.ONLINE);
        userRepo.save(user);
        return userDTO;
    }
    public User changeStatusOffline(User userDetails) {
        User account = userRepo.findByLogin(userDetails.getLogin());
        account.setStatus(Status.OFFLINE);
        return userRepo.save(account);
    }

    public List<UserDTO> findConnectedUsers() {
        List<User> accounts = userRepo.findByStatus(Status.ONLINE);
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User account: accounts) {
            UserDTO userDTO = new UserDTO();
            userDTO.setLogin(account.getLogin());

            userDTO.setRole(account.getRoles().toString());
            userDTO.setName(account.getName());
            userDTO.setSurname(account.getSurname());
            List<Message> chatMessage = messageRepo.findAllBySenderIdAndStatus(account, MessageStatus.DELIVERED);
            Set<ChatRoom> chatRoomId = new HashSet<>();
            for (Message chatMsg : chatMessage) {
                chatRoomId.add(chatMsg.getChatRoom());
            }
            for (ChatRoom chatRoom : chatRoomId) {
                ChatRoom chatRoomDB = chatRoomRepo.findById(chatRoom.getIdChatRoom()).orElse(null);
                if (chatRoomDB != null) {
                    ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
                    chatRoomDTO.setIdChatRoom(chatRoomDB.getIdChatRoom());
                    chatRoomDTO.setLoginUserSender(chatRoomDB.getSender().getLogin());
                    chatRoomDTO.setLoginUserRecipient(chatRoomDB.getRecipient().getLogin());
                    List<Message> chatMessageDB = messageRepo.findByChatRoom(chatRoomDB);
                    if (chatMessageDB != null) {
                        for (Message msg : chatMessageDB) {
                            chatRoomDTO.setMessageDTO(new MessageDTO(chatRoom.getIdChatRoom(),
                                    chatRoomDTO.getLoginUserSender(),
                                    msg.getSenderId().getSurname() + " " + msg.getSenderId().getName(),
                                    msg.getContent(), msg.getTimestamp(),
                                    msg.getStatus().toString()));
                        }
                    }
                    userDTO.setChatRoomDTOS(chatRoomDTO);
                }
            }
            userDTOList.add(userDTO);
        }
        return userDTOList;
    }

    public List<UserDTO> findOfflineUsers() {
        List<User> accounts = userRepo.findByStatus(Status.OFFLINE);
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User account: accounts) {
            UserDTO userDTO = new UserDTO();
            userDTO.setLogin(account.getLogin());

            userDTO.setRole(account.getRoles().toString());
            userDTO.setName(account.getName());
            userDTO.setSurname(account.getSurname());
            List<Message> chatMessage = messageRepo.findAllBySenderIdAndStatus(account, MessageStatus.DELIVERED);
            Set<ChatRoom> chatRoomId = new HashSet<>();
            for (Message chatMsg : chatMessage) {
                chatRoomId.add(chatMsg.getChatRoom());
            }
            for (ChatRoom chatRoom : chatRoomId) {
                ChatRoom chatRoomDB = chatRoomRepo.findById(chatRoom.getIdChatRoom()).orElse(null);
                if (chatRoomDB != null) {
                    ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
                    chatRoomDTO.setIdChatRoom(chatRoomDB.getIdChatRoom());
                    chatRoomDTO.setLoginUserSender(chatRoomDB.getSender().getLogin());
                    chatRoomDTO.setLoginUserRecipient(chatRoomDB.getRecipient().getLogin());
                    List<Message> chatMessageDB = messageRepo.findByChatRoom(chatRoomDB);
                    if (chatMessageDB != null) {
                        for (Message msg : chatMessageDB) {
                            chatRoomDTO.setMessageDTO(new MessageDTO(chatRoom.getIdChatRoom(),
                                    chatRoomDTO.getLoginUserSender(),
                                    msg.getSenderId().getSurname() + " " + msg.getSenderId().getName(),
                                    msg.getContent(), msg.getTimestamp(),
                                    msg.getStatus().toString()));
                        }
                    }
                    userDTO.setChatRoomDTOS(chatRoomDTO);
                }
            }
            userDTOList.add(userDTO);
        }
        return userDTOList;
    }

    public List<User> findUserWhoNotRead(int id) {
        User user = userRepo.getById(id);
        List<User> userList = new ArrayList<>();
        List<ChatRoom> listChat = chatRoomRepo.findBySenderOrRecipient(user,user);
        for (ChatRoom chat:listChat){
            List<Message> message = messageRepo.findByChatRoomAndSenderId(chat,
                    user.equals(chat.getSender())? chat.getRecipient() : chat.getSender());
            if(message.stream().map(Message::getStatus)
                    .filter(MessageStatus.DELIVERED::equals).count()>0){
                userList.add(userRepo.getById(user.equals(chat.getSender())? chat.getRecipient().getIdUser() : chat.getSender().getIdUser()));

            }
        }
        return userList;
    }

    public void disconnect(User user) {
        user.setStatus(Status.OFFLINE);
        userRepo.save(user);
    }

    public UserDTO getAuthUser(String login) {
        User account = userRepo.findByLogin(login);
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin(account.getLogin());
        userDTO.setRole(account.getRoles().toString());
        userDTO.setName(account.getName());
        userDTO.setSurname(account.getSurname());
        List<Message> chatMessage = messageRepo.findAllBySenderIdAndStatus(account, MessageStatus.DELIVERED);
        Set<ChatRoom> chatRoomId = new HashSet<>();
        for(Message chatMsg: chatMessage) {
            chatRoomId.add(chatMsg.getChatRoom());
        }
        for (ChatRoom chatRoom : chatRoomId){
            ChatRoom chatRoomDB = chatRoomRepo.findById(chatRoom.getIdChatRoom()).orElse(null);
            if (chatRoomDB!=null) {
                ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
                chatRoomDTO.setIdChatRoom(chatRoomDB.getIdChatRoom());
                chatRoomDTO.setLoginUserSender(chatRoomDB.getSender().getLogin());
                chatRoomDTO.setLoginUserRecipient(chatRoomDB.getRecipient().getLogin());
                List<Message> chatMessageDB = messageRepo.findByChatRoom(chatRoomDB);
                if (chatMessageDB!=null){
                    for (Message msg : chatMessageDB) {
                        chatRoomDTO.setMessageDTO(new MessageDTO(chatRoom.getIdChatRoom(),
                                chatRoomDTO.getLoginUserSender(),
                                msg.getSenderId().getSurname() + " " + msg.getSenderId().getName(),
                                msg.getContent(), msg.getTimestamp(),
                                msg.getStatus().toString()));
                    }
                }
                userDTO.setChatRoomDTOS(chatRoomDTO);
            }
        }
        account.setStatus(Status.ONLINE);
        userRepo.save(account);
        return userDTO;
    }
    public void changeStatusOnline(String login) {
        User account = userRepo.findByLogin(login);
        account.setStatus(Status.ONLINE);
        userRepo.save(account);
    }
    public void changeStatusOffline(String login) {
        User account = userRepo.findByLogin(login);
        account.setStatus(Status.OFFLINE);
        userRepo.save(account);
    }


}



