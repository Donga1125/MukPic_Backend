package com.I4U.mukpic.controller;

import com.I4U.mukpic.entity.User;
import com.I4U.mukpic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            String response = userService.registerUser(user);
            return ResponseEntity.ok(response); //정상처리
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); //유효성 검사 실패, 중복된 데이터의 경우
        } catch (Exception e) {
            return ResponseEntity.status(500).body("예기치 않은 오류가 발생했습니다.");
        }
    }

}
