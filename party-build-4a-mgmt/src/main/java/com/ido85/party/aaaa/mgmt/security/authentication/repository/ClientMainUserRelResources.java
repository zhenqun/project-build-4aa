//package com.ido85.party.aaaa.mgmt.security.authentication.repository;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
////import com.ido85.party.aaaa.mgmt.security.authentication.domain.ClientUserRel;
//import com.ido85.party.aaaa.mgmt.security.authentication.domain.UserClientRel;
//
//public interface ClientMainUserRelResources extends JpaRepository<UserClientRel, Long>{
//
//	@Query("select c from ClientUserRel c where c.clientId = :clientId and c.userId=:id")
//	List<UserClientRel> getCurByClientUser(@Param("clientId")String clientId,
//			@Param("id")String id);
//
//}
