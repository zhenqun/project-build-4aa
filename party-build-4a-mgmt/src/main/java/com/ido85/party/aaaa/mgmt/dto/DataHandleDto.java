package com.ido85.party.aaaa.mgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Administrator on 2017/9/12.
 */
@Data
public class DataHandleDto {
    public DataHandleDto() {
    }

    private String clientId;
    private String manageId;

    public DataHandleDto(String clientId, String manageId) {
        this.clientId = clientId;
        this.manageId = manageId;
    }
}
