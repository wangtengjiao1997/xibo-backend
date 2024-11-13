package com.npc.old_school.controller;

import com.npc.old_school.dto.auth.LoginDTO;
import com.npc.old_school.exception.ResultResponse;
import com.npc.old_school.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证管理", description = "用户认证相关接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Operation(summary = "用户登录", description = "用户通过用户名密码登录系统")
    @PostMapping("/login")
    public ResultResponse login(@Valid @RequestBody LoginDTO loginDTO) {
        return ResultResponse.success(authService.login(loginDTO));
    }
} 