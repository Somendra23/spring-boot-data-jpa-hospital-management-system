package com.codingshuttle.youtube.hospitalManagement.entity;

import lombok.*;
import org.springframework.stereotype.Service;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    private String username;
    private String password;

}
