package com.example.gmail2.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data


public class GmailinTransit {
    public String from;
    public String recipientUsername;
    public String message;

}