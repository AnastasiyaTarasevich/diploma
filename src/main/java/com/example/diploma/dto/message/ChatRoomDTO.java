package com.example.diploma.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDTO {
    private String idChatRoom;
    private String loginUserSender;
    private String loginUserRecipient;
    private List<MessageDTO> messageDTOList=new ArrayList<>();
    public void setMessageDTO(MessageDTO messageDTO)
    {
        this.messageDTOList.add(messageDTO);
    }
}
