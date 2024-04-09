package com.example.diploma.services;

import com.example.diploma.models.ChatRoom;
import com.example.diploma.models.Message;
import com.example.diploma.models.MessageStatus;
import com.example.diploma.models.User;
import com.example.diploma.repos.ChatRoomRepo;
import com.example.diploma.repos.MessageRepo;
import com.example.diploma.repos.UserRepo;
import com.example.diploma.dto.message.ChatRoomDTO;
import com.example.diploma.dto.message.MessageDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepo chatRoomRepository;
    private final UserRepo userRepository;
    private final MessageRepo messageRepository;
    public ChatRoomDTO findChatMessages(String senderLogin, String recipientLogin) {
        User sender = userRepository.findByLogin(senderLogin);
        User recipient = userRepository.findByLogin(recipientLogin);
        if (sender==null && recipient==null) return null;
        String chatId = senderLogin + " " + recipientLogin;;
        ChatRoom chatRoom = chatRoomRepository.findById(chatId).orElse(null);

//            if (chatRoom==null) {
//             createChatId(sender,recipient);
//            }
//        chatRoom = chatRoomRepository.findById(chatId).orElse(null);
        if (chatRoom==null){
            chatId = recipientLogin + " " + senderLogin;
            chatRoom = chatRoomRepository.findById(chatId).orElse(null);
            if (chatRoom==null) {
                chatRoom = new ChatRoom(chatId,
                        sender,
                        recipient,
                        null);
                chatRoom = chatRoomRepository.save(chatRoom);
            }
        }

        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        chatRoomDTO.setIdChatRoom(chatRoom.getIdChatRoom());
        chatRoomDTO.setLoginUserSender(chatRoom.getSender().getLogin());
        chatRoomDTO.setLoginUserRecipient(chatRoom.getRecipient().getLogin());
        if (chatRoom.getChatMessageList()!=null) {
            List<Message> messageList = messageRepository.findByChatRoom(chatRoom);
            for (Message msg : messageList) {
                chatRoomDTO.setMessageDTO(new MessageDTO(chatRoom.getIdChatRoom(),
                        chatRoomDTO.getLoginUserSender(),
                        msg.getSenderId().getSurname() + " " + msg.getSenderId().getName(),
                        msg.getContent(), msg.getTimestamp(),
                        msg.getStatus().toString()));
                msg.setStatus(MessageStatus.RECEIVED);
                messageRepository.save(msg);
            }
        }
        return chatRoomDTO;
    }

    public List<ChatRoomDTO> findChatMessagesSender(String senderLogin) {
        User sender = userRepository.findByLogin(senderLogin);
        if (sender==null ) return null;
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByIdChatRoomLike(senderLogin);
        if (chatRoomList==null){
            return null;
        }
        List<ChatRoomDTO> chatRoomDTOList = new ArrayList<>();
        for(ChatRoom chatRoom :chatRoomList){
            ChatRoomDTO chatRoomDTO =new ChatRoomDTO();
            chatRoomDTO.setIdChatRoom(chatRoom.getSender().getLogin() + " " + chatRoom.getRecipient().getLogin());
            chatRoomDTO.setLoginUserSender(chatRoom.getSender().getLogin());
            chatRoomDTO.setLoginUserRecipient(chatRoom.getRecipient().getLogin());
            List<Message> messageList = messageRepository.findByChatRoom(chatRoom);
            for (Message msg : messageList) {
                chatRoomDTO.setMessageDTO(new MessageDTO(chatRoom.getIdChatRoom(),
                        chatRoomDTO.getLoginUserSender(),
                        msg.getSenderId().getSurname() + " " + msg.getSenderId().getName(),
                        msg.getContent(), msg.getTimestamp(),
                        msg.getStatus().toString()));
            }
            chatRoomDTOList.add(chatRoomDTO);
        }
        return chatRoomDTOList;
    }
//    public void  createChatId(User senderId, User recipientId) {
//        var chatId=String.format("%s %s", senderId.getLogin(),recipientId.getLogin());
//        ChatRoom senderRecipient =ChatRoom.builder()
//                .idChatRoom(chatId)
//                .sender(senderId)
//                .recipient(recipientId)
//                .build();
//        ChatRoom recipientSender =ChatRoom.builder()
//                .idChatRoom(chatId)
//                .sender(recipientId)
//                .recipient(senderId)
//                .build();
//        chatRoomRepository.save(senderRecipient);
//        chatRoomRepository.save(recipientSender);
//    }
}
