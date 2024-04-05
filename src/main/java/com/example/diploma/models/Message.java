package com.example.diploma.models;


import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User sender;

   @ManyToOne
    private User recipient;
    @ManyToOne
    private ChatRoom chatRoom;
    @Column
    private String content;
    @Column
    private Date timestamp;
    @Column
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

}
