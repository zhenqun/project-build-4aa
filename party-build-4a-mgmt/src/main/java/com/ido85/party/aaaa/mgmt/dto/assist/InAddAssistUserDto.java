package com.ido85.party.aaaa.mgmt.dto.assist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Administrator on 2017/10/9.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InAddAssistUserDto {

    @NotNull
    private String idCard;
    @NotNull
    private String relName;
    @NotNull
    private String telephone;
    @NotEmpty
    private List<AddAssistUserScopeParam> clients;
}
