package com.npc.old_school.dto.weixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeixinTokenResponse {
    private Integer errcode;
    private String errmsg;
    private String access_token;
    @JsonProperty("expires_in")
    private Integer expiresIn;
}