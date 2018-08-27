package com.ido85.party.aaaa.mgmt.dto.assist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Administrator on 2017/10/13.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssistStateDto {

    private List<String> fzuserIds;

    private String state;
}
