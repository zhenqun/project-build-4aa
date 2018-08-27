package com.ido85.party.aaa.authentication.ncii.serv;



import javax.tools.FileObject;

import com.ido85.party.aaa.authentication.ncii.domain.FileObjectSetPhone;
import com.ido85.party.aaa.authentication.ncii.vo.ServLtVO;


public interface ServiceInf {

	public String nciicAddrExactSearch(String inLicense, String inConditions)throws Exception;

	public String nciicCheck(String inLicense, String inConditions)throws Exception;

	public String nciicCompare(String inLicense, String inConditions)throws Exception;

	public String nciicExactSearch(String inLicense, String inConditions)throws Exception;
	
	public String nciicCourt(String inLicense,String inConditions) throws Exception;
	
	public String nciicCombineSearch(String inLicense, String inConditions)throws Exception;

	public String nciicStat(String inLicense, String inConditions)throws Exception;

	public String nciicDiscern(String inLicense, String inConditions)throws Exception;

	public String nciicGetCondition(String inLicense) throws Exception;
	
	public String nciicCheckChina(String inLicense, String inConditions)throws Exception;
	
	public String nciicCheckHotel(String inLicense, String inConditions)throws Exception;
	
	public String nciicBirthplaceCompare(String inLicense, String inConditions) throws Exception;
	
	public String nciicCheckFile(FileObject fileObject) throws Exception;
	
	public FileObject nciicGetFile(String inLicense, String id) throws Exception;
	
	public String tmtx(java.lang.String licensecode, java.lang.String condition) throws Exception;
	
	public String nciicCheckMobile(String inLicense, String inConditions)throws Exception;
	
	public String wmrzCheck(String inLicense, String inConditions)throws Exception;

	public FileObjectSetPhone nciicCheckSetPhone(FileObjectSetPhone fileObject) throws Exception;
	public FileObjectSetPhone nciicCheckGetPhone(String inLicense, String id) throws Exception;
	
	public String nciicCheckScan(String inLicense, String inConditions,String keys)throws Exception;
	
	public ServLtVO chinaUnicomGetPhone(ServLtVO vo) throws Exception;
	
	public String faceProxy(int compressFlag,String galleryBase64Image,
			int galleryMaxHeight,int galleryMaxSize,
			String probeBase64Image,int probeImgType,
			int probeMaxHeight,int probeMaxSize)throws Exception;

	public String nciicDateCollection(String inLicense, String inConditions)throws Exception;

	
}
