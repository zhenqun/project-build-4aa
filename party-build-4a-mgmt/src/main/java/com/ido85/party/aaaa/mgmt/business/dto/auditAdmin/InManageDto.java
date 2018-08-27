package com.ido85.party.aaaa.mgmt.business.dto.auditAdmin;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Created by Administrator on 2017/10/18 0018.
 */
@Data
public class InManageDto {
    @NotEmpty
    private List<InReasonDto> items;

    private String rerson;//后端用  前端不用传
    private List<String>itemIds;//后端用  前端不用传
    private String userId;//后端用  前端不用传
}
