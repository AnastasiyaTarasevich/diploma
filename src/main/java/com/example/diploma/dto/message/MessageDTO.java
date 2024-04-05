package com.example.diploma.dto.message;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private String idChatRoom;
    private String senderLogin;
    private String content;
    private Date timestamp;
    private String status;
}
