package com.npc.xibo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("public.employee")
public class Employee {

    @TableId
    private Long id;
    private String name;
    private String nickname;
    @TableField(value = "userid")
    private String userid;
    private String gender;
    private String contactPhone;
    private Boolean subAccount;
    private String imagePath;
    private String imageUrl;
    private String relateAccountUid;
}
