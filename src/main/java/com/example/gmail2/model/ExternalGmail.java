package com.example.gmail2.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalGmail {
    private String fromUserName;
    private String toUserName;
    private String message;
}
