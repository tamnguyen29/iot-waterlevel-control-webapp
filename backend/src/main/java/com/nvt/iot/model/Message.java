package com.nvt.iot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String sender;
    private Action action;
    private Object content;
    private String receiver;
    private Date time;
}
