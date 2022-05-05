package com.francin.linux.common;

import org.springframework.stereotype.Component;

import static com.francin.linux.pojo.UserTask.CHALLENGE_MODE;

@Component
public class Utils {

    public String replaceRes(String origin, String res) {
        if (origin == null) return "";
        origin = origin.replaceAll("\\{\\{res\\}\\}",res);
        return origin;
    }

    public String replace(String origin, String replace, int mode){

        if(origin==null)return "";

        if (mode == CHALLENGE_MODE) { // 挑战模式逆序随机数
            StringBuffer sb = new StringBuffer(replace);
            replace = sb.reverse().toString();
        }

        origin = origin.replaceAll("\\{\\{\\d+\\}\\}",replace);
//
//        String[] replaces = replace.split(";");
//        for (int i = 0; i < replaces.length; i++) {
//            origin = origin.replaceAll("\\{\\{\\d+\\}\\}",replaces[i]);
//        }

        return origin;
    }
}
