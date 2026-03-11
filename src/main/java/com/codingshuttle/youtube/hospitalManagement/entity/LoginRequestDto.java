package com.codingshuttle.youtube.hospitalManagement.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Data
@Getter
@Setter
public class LoginRequestDto {
    private String username;
    private String password;

}
