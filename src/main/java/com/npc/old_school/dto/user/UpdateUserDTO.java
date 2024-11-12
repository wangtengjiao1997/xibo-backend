package com.npc.old_school.dto.user;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
public class UpdateUserDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    private String password; // 可选
    private String confirmPassword; // 可选
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @NotNull(message = "角色不能为空")
    private Long roleId;
    
    @NotBlank(message = "管理区不能为空")
    private String district;
    
    @NotNull(message = "管理站点不能为空")
    private Long serviceSiteId;
    
    @NotNull(message = "状态不能为空")
    private Boolean status;
} 