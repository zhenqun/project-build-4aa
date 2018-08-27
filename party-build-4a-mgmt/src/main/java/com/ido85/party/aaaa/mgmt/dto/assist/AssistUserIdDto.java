package com.ido85.party.aaaa.mgmt.dto.assist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2017/10/11.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssistUserIdDto {

    @NotNull
    private String fzuserId;

}
