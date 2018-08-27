package com.ido85.party.sso.dto;

import java.io.Serializable;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
@Data
public class HeadpicUploadDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, MultipartFile> fileMap;
	
	private String userId;

}
