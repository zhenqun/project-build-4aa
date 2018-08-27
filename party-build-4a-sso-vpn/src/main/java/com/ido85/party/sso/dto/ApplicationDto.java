package com.ido85.party.sso.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
@Data
public class ApplicationDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String categoryId;
	
	private String categoryName;
	
	private String categoryOrder;
	
	private String type;
	
	private List<ApplicationDetailDto> apps;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApplicationDto other = (ApplicationDto) obj;
		if (categoryId == null) {
			if (other.categoryId != null)
				return false;
		} else if (!categoryId.equals(other.categoryId))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoryId == null) ? 0 : categoryId.hashCode());
		return result;
	}

	
}
