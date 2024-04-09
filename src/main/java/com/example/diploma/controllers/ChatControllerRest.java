package com.example.diploma.controllers;


import com.example.diploma.dto.message.UserDTO;
import com.example.diploma.dto.message.ChatRoomDTO;
import com.example.diploma.models.User;
import com.example.diploma.services.ChatRoomService;
import com.example.diploma.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatControllerRest {
    private final UserService accountService;
    private final ChatRoomService chatRoomService;
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> findConnectedUsers() {
        return ResponseEntity.ok(accountService.findConnectedUsers());
    }

    @GetMapping("/offlineUsers")
    public ResponseEntity<List<UserDTO>> findOfflineUsers()
    {
        return ResponseEntity.ok(accountService.findOfflineUsers());
    }
    @GetMapping("/findChat/{idUser}")
    public ResponseEntity<List<User>> findConnectedUsers(@PathVariable String idUser) {
        return ResponseEntity.ok(accountService.findUserWhoNotRead(Integer.valueOf(idUser)));
    }

    @GetMapping("/messages/{sender}/{recipient}")
    public ResponseEntity<ChatRoomDTO>  findChatMessages(@PathVariable("sender") String senderLogin,
                                                         @PathVariable("recipient") String recipientLogin) {
        return ResponseEntity.ok(chatRoomService.findChatMessages(senderLogin, recipientLogin));
    }
    @GetMapping("/messages/{sender}")
    public ResponseEntity<List<ChatRoomDTO>> findChatMessages(@PathVariable("sender") String senderLogin ){
        return ResponseEntity.ok(chatRoomService.findChatMessagesSender(senderLogin));
    }
    @GetMapping("/getAuthUser")
    public UserDTO getAuthUser(Authentication authentication){
        String login = authentication.getName();
        return accountService.getAuthUser(login);
    }
}