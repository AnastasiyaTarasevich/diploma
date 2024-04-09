package com.example.diploma.services;


import com.example.diploma.dto.message.ChatRoomDTO;
import com.example.diploma.dto.message.MessageDTO;
import com.example.diploma.models.ChatRoom;
import com.example.diploma.models.Message;
import com.example.diploma.models.MessageStatus;
import com.example.diploma.models.User;
import com.example.diploma.repos.ChatRoomRepo;
import com.example.diploma.repos.MessageRepo;
import com.example.diploma.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final MessageRepo messageRepository;
    private final ChatRoomRepo chatRoomRepository;
    private final UserRepo userRepository;
    private final ChatRoomService chatRoomService;
    public Message save(Message chatMessage) {
        chatMessage.setStatus(MessageStatus.DELIVERED);
        return messageRepository.save(chatMessage);
    }

    public Message saveMessage(MessageDTO messageDTO) {
        Message message = new Message();
        message.setStatus(MessageStatus.DELIVERED);
       User account = userRepository.findByLogin(messageDTO.getSenderLogin());

        User accountRep = new User();
        String[] words = messageDTO.getIdChatRoom().split(" ");
        for (String word : words) {
            if (!word.equals(account.getLogin())) {
                accountRep = userRepository.findByLogin(word);
            }
        }
//        ChatRoom chatRoom = chatRoomRepository.findBySenderAndRecipient(account,recipient).orElse(null);
//        if (chatRoom == null) {
//            chatRoomService.createChatId(account,recipient);
//
//        }
//        chatRoom = chatRoomRepository.findBySenderAndRecipient(account,recipient).orElse(null);
        ChatRoom chatRoom = chatRoomRepository.findById(words[0] + " " + words[1]).orElse(null);
        if (chatRoom == null) {
            chatRoom = chatRoomRepository.findById(words[1] + " " + words[0]).orElse(null);
        }
        message.setChatRoom(chatRoom);
        message.setSenderId(account);

        message.setContent(messageDTO.getContent());
        message.setTimestamp(messageDTO.getTimestamp());
        return messageRepository.save(message);

    }
}