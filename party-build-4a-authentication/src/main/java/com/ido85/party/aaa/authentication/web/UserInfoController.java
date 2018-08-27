///**
// * 
// */
//package com.ido85.party.aaa.authentication.web;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Map;
//
//import javax.inject.Inject;
//import javax.servlet.ServletException;
//import javax.validation.Valid;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.ido85.party.aaa.authentication.dto.HeadpicUploadDto;
//import com.ido85.party.aaa.authentication.reposities.UserResources;
//import com.ido85.party.platform.distribute.generator.IdGenerator;
//import com.ido85.party.platform.utils.StringUtils;
//
//
///**
// * 
// * @author rongxj
// *
// */
//@RestController
//public class UserInfoController {
//
//	@Inject
//	private UserResources userResource;
//	
//	@Inject
//	private IdGenerator idGenerator;
//	
//	@Value("${userLogoUrl}")
//	private String userLogoUrl;
//	
//	@Value("${uploadLogoUrl}")
//	private String uploadLogoUrl;
//	
//	@Value("${defaultUserLogoUrl}")
//	private String defaultUserLogoUrl;
//	
//	@RequestMapping(value = { "/internet/headpicUpload" }, method = RequestMethod.POST)
//	@ResponseBody
//	public String Upload(@Valid HeadpicUploadDto param) throws ServletException {
//		Map<String, MultipartFile> fileMap = param.getFileMap();
//		String userId = param.getUserId();
//		String fileName = null;
//		InputStream ins = null;
//		FileOutputStream fos = null;
//		if(StringUtils.isNull(userId)){
//			return null;
//		}
//		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
//			MultipartFile mf = entity.getValue();
//			if(null != mf){
//				fileName = mf.getOriginalFilename();
//			}
//			try {
//				String suffix = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf("."), fileName.length()) : null;
//				fileName = StringUtils.toString(idGenerator.next())+suffix;
//				ins = mf.getInputStream();
//				fos = new FileOutputStream(uploadLogoUrl + fileName);
//				byte[] b = new byte[1024];
//				int len = 0;
//				while ((len = ins.read(b)) != -1) {
//					fos.write(b, 0, len);
//				}
//				int flag = userResource.modifyUserLogo(userId,fileName);
//				if(flag == 1){
//					if(!StringUtils.isNull(fileName)){
//						return userLogoUrl+fileName;
//					}
//				}
//				return null;
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					if(null != ins){
//						ins.close();
//					}
//					if(null != fos){
//						fos.close();
//					}
//				} catch (Exception e2) {
//					return null;
//				}
//			}
//		}
//		return null;
//	}
//}
