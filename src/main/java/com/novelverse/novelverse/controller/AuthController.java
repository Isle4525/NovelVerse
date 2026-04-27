package com.novelverse.novelverse.controller;


import com.novelverse.novelverse.dto.novel.CreateNovelDTO;
import com.novelverse.novelverse.dto.user.LoginDTO;
import com.novelverse.novelverse.dto.user.RegisterDTO;
import com.novelverse.novelverse.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO.username, registerDTO.password);
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginDTO loginDTO) {
        userService.login(loginDTO.username, loginDTO.password);
    }
}
