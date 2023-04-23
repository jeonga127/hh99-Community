package com.sparta.hanghaememo.controller;

import com.sparta.hanghaememo.dto.user.LoginRequestDto;
import com.sparta.hanghaememo.dto.user.SignupRequestDto;
import com.sparta.hanghaememo.security.UserDetailsImpl;
import com.sparta.hanghaememo.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) throws Exception {
        return userService.login(loginRequestDto,response);
    }

    @DeleteMapping("/withdrawl")
    public ResponseEntity withdrawl(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.withdrawal(userDetails.getUser());
    }

}
