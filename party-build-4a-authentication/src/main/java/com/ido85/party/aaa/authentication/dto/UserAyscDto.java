package com.ido85.party.aaa.authentication.dto;

import com.ido85.party.aaa.authentication.entity.User;
import lombok.Data;

/**
 * Created by Administrator on 2017/8/29.
 */
@Data
public class UserAyscDto {

    private String authUserId;

    private User user;

    public UserAyscDto(String authUserId, User user) {
        this.authUserId = authUserId;
        this.user = user;
    }
}
