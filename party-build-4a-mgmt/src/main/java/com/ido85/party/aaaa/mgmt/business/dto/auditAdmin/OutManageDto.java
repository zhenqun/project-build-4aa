package com.ido85.party.aaaa.mgmt.business.dto.auditAdmin;

import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import lombok.Data;

/**
 * Created by Administrator on 2017/10/10 0010.
 */
@Data
public class OutManageDto {
    private String id;//业务管理员申请表id
    private String clientId;
    private String clientName;
    private String manageId;
    private String manageCode;
    private String manageName;
    private Long roleIdl;
    private String roleId;
    private String roleName;
    private String itemId;


    public OutManageDto(String id, String clientId, String clientName, String manageId, String manageCode, String manageName, Long roleIdl, String roleName, String itemId) {
        this.id = id;
        this.clientId = clientId;
        this.clientName = clientName;
        this.manageId = manageId;
        this.manageCode = manageCode;
        this.manageName = manageName;
        this.roleIdl = roleIdl;
        this.roleName = roleName;
        this.itemId = itemId;
        this.roleId=StringUtils.toString(roleIdl);
    }

    public OutManageDto(String clientId, String manageId) {
        this.clientId = clientId;
        this.manageId = manageId;
    }

    public OutManageDto() {
    }
}
