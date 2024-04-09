package com.example.diploma.controllers;

import com.example.diploma.dto.message.MessageDTO;
import com.example.diploma.dto.message.UserDTO;

import com.example.diploma.models.Message;
import com.example.diploma.models.User;
import com.example.diploma.services.ChatMessageService;
import com.example.diploma.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final UserService accountService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chat.sendMessage")
    public MessageDTO sendMessage(@Payload MessageDTO MessageDTO) {
        Message chatMessage = chatMessageService.saveMessage(MessageDTO);
        String login = MessageDTO.getSenderLogin().equals(chatMessage.getChatRoom().getSender().getLogin()) ?
                chatMessage.getChatRoom().getRecipient().getLogin() :
                chatMessage.getChatRoom().getSender().getLogin();
        messagingTemplate.convertAndSendToUser(login, "/queue/messages", MessageDTO);
        return MessageDTO;
    }
    @MessageMapping("/user.addUser")
    @SendTo("/user/public")
    public UserDTO addUser(
            @Payload User user
    ) {
        return accountService.changeStatusOnline(user);

    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/public")
    public User disconnectUser(
            @Payload User user
    ) {
        accountService.disconnect(user);
        return user;
    }




}