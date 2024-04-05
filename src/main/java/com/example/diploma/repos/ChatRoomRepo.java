package com.example.diploma.repos;

import com.example.diploma.models.ChatRoom;
import com.example.diploma.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepo extends JpaRepository<ChatRoom,String> {
    Optional<ChatRoom> findBySenderAndRecipient(User sender, User recipient);
    List<ChatRoom> findAllByIdChatRoomLike(String idChatRoom);
    List<ChatRoom> findBySenderOrRecipient(User sender, User recipient);
}
