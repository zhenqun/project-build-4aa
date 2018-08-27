package com.ido85.party.sso.security.authentication.dto;

import lombok.Data;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by fire on 2017/2/22.
 */
@Data
public class InQueryUserInfoDto {
	private List<String> hashs;
}
