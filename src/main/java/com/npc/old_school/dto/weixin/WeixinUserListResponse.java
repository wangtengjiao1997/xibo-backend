package com.npc.old_school.dto.weixin;

import lombok.Data;
import java.util.List;

@Data
public class WeixinUserListResponse {
    private Integer errcode;
    private String errmsg;
    private List<DeptUser> dept_user;

    @Data
    public static class DeptUser {
        private String userid;
        private Integer department;
    }
}