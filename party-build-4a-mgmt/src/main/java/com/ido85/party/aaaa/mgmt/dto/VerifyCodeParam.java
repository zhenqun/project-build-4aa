package com.ido85.party.aaaa.mgmt.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

@Data
public class VerifyCodeParam implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4709672883176317781L;
	@NotBlank
	private String telephone;
	@NotBlank
	private String type;
	@NotBlank
	private String veifyCode;

}