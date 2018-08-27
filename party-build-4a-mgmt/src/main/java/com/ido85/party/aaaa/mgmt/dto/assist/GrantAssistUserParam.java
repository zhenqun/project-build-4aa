package com.ido85.party.aaaa.mgmt.dto.assist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Administrator on 2017/10/11.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrantAssistUserParam {

    @NotNull
    private  String fzuserId;//辅助安全员id

    private List<GrantAssistClients> clients;
}
