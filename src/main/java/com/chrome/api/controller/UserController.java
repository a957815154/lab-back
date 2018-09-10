package com.chrome.api.controller;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.chrome.api.dto.ResponseTemplate;
import com.chrome.api.service.UserService;
import com.chrome.domain.entity.User;
import com.chrome.infra.annotation.AuthToken;
import com.chrome.infra.util.ConstantKit;
import com.chrome.infra.util.Md5TokenGenerator;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

/**
 * Created with IDEA
 * author:Chengcong
 * Date:2018/9/8
 * Time:9:48
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/")
public class UserController {

    @Autowired
    Md5TokenGenerator tokenGenerator;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ApiOperation("用户登录接口")
    public ResponseTemplate login(@RequestBody(required = false) JSONObject userInfo) {

        String username = userInfo.getString("username");
        String password = userInfo.getString("password");
        User user = new User();
        user.setUserName(username);
        user.setUserPassword(tokenGenerator.passwordMd5(password));
        User currentUser = userService.login(user);


        JSONObject result = new JSONObject();
        String token=null;
        String message=null;
        if (currentUser != null) {

            Jedis jedis = new Jedis("localhost", 6379);
            token = tokenGenerator.generate(username, password);
            jedis.set(username, token);
            jedis.expire(username, ConstantKit.TOKEN_EXPIRE_TIME);
            jedis.set(token, username);
            jedis.expire(token, ConstantKit.TOKEN_EXPIRE_TIME);
            Long currentTime = System.currentTimeMillis();
            jedis.set(token + username, currentTime.toString());

            //用完关闭
            jedis.close();

          message="登陆成功";
        } else {
            message="登陆失败";
        }

        return ResponseTemplate.builder()
                .code(200)
                .message(message)
                .data(token)
                .build();

    }

    @ApiOperation("测试token接口")
    @RequestMapping(value = "test", method = RequestMethod.GET)
    @AuthToken
    public ResponseTemplate test() {
        List<User> user = userService.selectAll();
        return ResponseTemplate.builder()
                .code(200)
                .message("Success")
                .data(user)
                .build();
    }
}
