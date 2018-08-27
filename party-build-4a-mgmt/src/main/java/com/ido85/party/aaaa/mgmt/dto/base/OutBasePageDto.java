package com.ido85.party.aaaa.mgmt.dto.base;

import java.util.List;

/**
 * 页面返回的page基类
 */
public class OutBasePageDto<T> {
	protected Integer pageSize;
	protected Integer pageNo;
	protected Long count;
	protected List<T> data;
	
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageNo() {
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		if(null == count){
			count = 0l;
		}
		this.count = count;
	}
	public OutBasePageDto(Integer pageSize, Integer pageNo, Long count) {
		super();
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		this.count = count;
	}
	public OutBasePageDto() {
		super();
	}

	
}
