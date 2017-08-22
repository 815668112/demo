package com.example.demo.controller;

import com.example.demo.config.DBHolder;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Administrator on 2017/8/20.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {
        if (userId == 1) {
            DBHolder.setDb("my");
        } else if (userId == 2) {
            DBHolder.setDb("gd");
        }
        return userService.findOne(userId);
    }

    @GetMapping
    public Iterable<User> getUsers() {
        return userService.findAll();
    }

    @PostMapping
    public User createUser(@ModelAttribute User user) {
        user = userService.save(user);
        return user;
    }
}
