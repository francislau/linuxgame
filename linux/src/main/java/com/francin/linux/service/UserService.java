package com.francin.linux.service;

import com.francin.linux.pojo.User;

public interface UserService {
    User login(User user);

    String home(int id);

    int chgPsw(User user);
}
