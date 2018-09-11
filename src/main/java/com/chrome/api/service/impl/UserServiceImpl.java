package com.chrome.api.service.impl;

import java.util.List;

import com.chrome.api.service.UserService;
import com.chrome.domain.entity.User;
import com.chrome.infra.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IDEA
 * author:Chengcong
 * Date:2018/9/10
 * Time:11:28
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

//登陆
    @Override
    public User login(User user) {
        return userMapper.selectOne(user);

    }
//根据用户名获得当前用户
    @Override
    public User selectByUsername(String username) {
        User user=new User();
        user.setUserName(username);
        return userMapper.selectOne(user);
    }
//列出所有用户
    @Override
    public List<User> selectAll() {
        return userMapper.selectAll();
    }
}
