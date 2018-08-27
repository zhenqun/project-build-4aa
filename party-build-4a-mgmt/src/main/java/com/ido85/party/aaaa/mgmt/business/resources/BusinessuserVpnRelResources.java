package com.ido85.party.aaaa.mgmt.business.resources;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.business.domain.BusinessUserVpnRel;

import javax.transaction.Transactional;
import java.util.List;

public interface BusinessuserVpnRelResources extends JpaRepository<BusinessUserVpnRel, Long>{

	@Query("select l from BusinessUserVpnRel l where l.userId = :id")
	BusinessUserVpnRel getRelByUserId(@Param("id")String id);

	@Query("select l.ou from BusinessUserVpnRel l where l.userId = :id")
	String getOuNameByUserId(@Param("id") String userId);

	@Query("select l from BusinessUserVpnRel l where l.userId in :ids")
	List<BusinessUserVpnRel> getRelByUserIds(@Param("ids") List<String> only1List);

	@Query("select l.userId from BusinessUserVpnRel  l where l.vpn =:idCard")
	String getVpnByIdCard(@Param("idCard")String idCard);

	@Query("select l from BusinessUserVpnRel  l where l.vpn = :idCard")
	BusinessUserVpnRel getVpnUserByIdCard(@Param("idCard")String idCard);

	@Transactional
	@Modifying
	@Query(value = "update BusinessUserVpnRel u set userId =:userId where vpn=:vpn and ou =:ou")
	int updateVpnByIdCard(@Param("userId")String userId,@Param("vpn")String vpn,@Param("ou")String ou);


}
