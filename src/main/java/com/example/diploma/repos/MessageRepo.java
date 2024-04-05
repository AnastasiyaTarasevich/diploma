package com.example.diploma.repos;

import com.example.diploma.models.ChatRoom;
import com.example.diploma.models.Message;
import com.example.diploma.models.MessageStatus;
import com.example.diploma.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepo extends JpaRepository<Message, Long> {

    boolean existsBySenderIdAndStatus(User senderId, MessageStatus status);

    List<Message> findBySenderId(User senderId);
    List<Message> findByChatRoomAndSenderId(ChatRoom chatRoom, User senderId);
    List<Message> findAllBySenderIdAndStatus(User senderId, MessageStatus status);

    List<Message> findByChatRoom(ChatRoom chatRoom);
}
