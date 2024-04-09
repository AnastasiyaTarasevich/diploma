package com.example.diploma.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String login;
    private String Role;
    private String name;
    private String surname;
    private List<ChatRoomDTO>chatRoomDTOS=new ArrayList<>();
    public void setChatRoomDTOS(ChatRoomDTO chatRoomDTO)
    {
        this.chatRoomDTOS.add(chatRoomDTO);
    }

}
