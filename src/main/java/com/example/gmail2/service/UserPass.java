package com.example.gmail2.service;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserPass {
    public String username;
    public String password;


}
