package com.ido85.party.aaaa.mgmt.dto.role;

import com.ido85.party.aaaa.mgmt.security.utils.DateUtils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class RoleQueryDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String roleId;//角色id
	
	private String roleName;//角色名称
	
	private String roleDescription;//角色描述
	
	private String createDate;//创建时间
	
	private String createBy;//创建人
	
	private String updateBy;//最后一次更新人
	
	private String updateDate;//最后一次更新时间

	private boolean isCommon;

	private String detail;

	public RoleQueryDto(Long roleId, String roleName, String roleDescription, Date createDate, String createBy,
			String updateBy, Date updateDate,boolean isCommon,String detail) {
		super();
		this.roleId = StringUtils.toString(roleId);
		this.roleName = roleName;
		this.roleDescription = roleDescription;
		this.createDate = DateUtils.formatDate(createDate,"yyyy-MM-dd HH:mm:ss");
		this.createBy = createBy;
		this.updateBy = updateBy;
		this.updateDate = DateUtils.formatDate(updateDate,"yyyy-MM-dd HH:mm:ss");
		this.isCommon = isCommon;
		this.detail = detail;
	}
	
	
}
