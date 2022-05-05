package com.francin.linux.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDevice {

    private long id;
    private long userId;
    private String connPass;
    private String conn;
    private String keyCode;
    private String connUnverified;
    private String keyCodeUnverified;
    private String verifyMsg;
    private String connPractise;
    private long status;
    private Timestamp createAt;
    private Timestamp updateAt;
    private Timestamp createBy;
    private Timestamp updateBy;

}
