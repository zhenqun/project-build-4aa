package com.ido85.party.aaaa.mgmt.dto.assist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Created by Administrator on 2017/10/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyUserIdsDto {
    @NotEmpty
    private List<String> itemIds;
}
