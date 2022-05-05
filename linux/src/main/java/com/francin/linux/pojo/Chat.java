package com.francin.linux.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    private int id;
    private int userId;
    private String realname;
    private String content;

    private User user;
}
