package com.ido85.party.aaaa.mgmt.internet.dto;

import com.ido85.party.aaaa.mgmt.dto.base.InBasePageDto;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by fire on 2017/3/1.
 */
@Data
public class InRegisterLogQueryDto extends InBasePageDto {
	private String endDate;
	@NotBlank
	private String logFrom;
	private String logOperator;
	private String result;
	private String startDate;
}
