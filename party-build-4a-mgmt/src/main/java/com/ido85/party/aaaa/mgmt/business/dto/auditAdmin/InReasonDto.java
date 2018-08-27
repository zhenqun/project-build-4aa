package com.ido85.party.aaaa.mgmt.business.dto.auditAdmin;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Administrator on 2017/10/18 0018.
 */
@Data
public class InReasonDto {
    @NotBlank
    private String itemId;
    @NotBlank
    private String reason;
}
