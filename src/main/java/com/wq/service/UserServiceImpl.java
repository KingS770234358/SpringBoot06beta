package com.wq.service;

import com.wq.mapper.UserMapper;
import com.wq.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/***
 * 服务层组件注解 @Service
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper UserMapper;

    @Override
    public User queryUserByName(String name) {
        return UserMapper.queryUserByName(name);
    }
}
