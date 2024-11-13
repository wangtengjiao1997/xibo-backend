package com.npc.old_school.dto.xibo;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class EmployeeDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;
    private String nickname;
    @NotBlank(message = "工号不能为空")
    private String uid;
    @NotBlank(message = "性别不能为空")
    private String gender;
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;
    // 员工照片路径
    private String imagePath;
    private String imageUrl;
    private Boolean subAccount;
}
