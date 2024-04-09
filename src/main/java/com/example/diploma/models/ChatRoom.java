package com.example.diploma.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatRoom {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
    private String idChatRoom;
    @ManyToOne(fetch = FetchType.EAGER)
    private User sender;
    @ManyToOne(fetch = FetchType.EAGER)
    private User recipient;
    @OneToMany
    private List<Message> chatMessageList;


}
