package com.francin.linux.service;

import com.francin.linux.pojo.User;
import com.jcraft.jsch.JSchException;

import java.io.IOException;

public interface SshService {

    String exec(String host, int port, String username, String password, String command) throws JSchException, IOException;

}
