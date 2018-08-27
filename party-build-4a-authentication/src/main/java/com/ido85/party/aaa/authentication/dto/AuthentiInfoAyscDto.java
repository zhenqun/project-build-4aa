package com.ido85.party.aaa.authentication.dto;

import com.ido85.party.aaa.authentication.entity.AuthenticationInfo;
import com.ido85.party.aaa.authentication.entity.User;
import com.ido85.party.platform.utils.StringUtils;
import lombok.Data;

/**
 * Created by Administrator on 2017/8/21.
 */
@Data
public class AuthentiInfoAyscDto {
    private String userId;

    private String idCard;

    private String relName;

    private String clientId;

    private String roleId;

    private String validFlag;


    public boolean equals(Object obj1) {
        if(null !=obj1 && obj1 instanceof AuthenticationInfo){
            AuthenticationInfo info = (AuthenticationInfo) obj1;
            if(StringUtils.toString(userId).equals(info.getUserId()) &&
                    StringUtils.toString(clientId).equals(info.getClientId()) &&
                        StringUtils.toLong(roleId).equals(info.getRoleId().longValue())){
                return true;
            }
        }
        if(null !=obj1 && obj1 instanceof UserAyscDto){
            UserAyscDto userAyscDto = (UserAyscDto) obj1;
            if(StringUtils.toString(userId).equals(userAyscDto.getAuthUserId())){
                return true;
            }
        }
        return  false;
    }

}
