package com.ido85.party.aaaa.mgmt.dto.base;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 页面查询的page基类
 */
@Data
public class InBasePageDto {
	@NotNull
	protected Integer pageSize = 20;
	@NotNull
	protected Integer pageNo = 1;
	protected Integer count;

	public Integer getDatabasePageNo(){
		if(this.pageNo <= 0){
			return 0;
		}
		return this.pageNo-1;
	}
	
}
