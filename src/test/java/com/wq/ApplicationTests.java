package com.wq;

import com.wq.service.UserService;
import com.wq.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

    @Autowired
    UserService userService;
    @Test
    void contextLoads() {
        System.out.println(userService.queryUserByName("狂神3.0"));
    }

}
