package com.npc.old_school.dto.xibo;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class EmployeeDTO {
    @NotBlank(message = "工号不能为空")
    private String userid;
    private String name;
    private String nickname;
    private String gender;
    private String contactPhone;
    // 员工照片路径
    private String imagePath;
    private String imageUrl;
    private Boolean subAccount;
    private String relateAccountUid;
}
