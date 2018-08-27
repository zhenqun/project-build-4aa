/**
 * 
 */
package com.ido85.party.aaa.authentication.reposities;

import com.ido85.party.aaa.authentication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * user 资源类
 * @author rongxj
 *
 */
public interface UserResources extends JpaRepository<User, String> {

    @Query("select u from User u where u.id in :uIds")
    List<User> getUsersByIds(List<Long> uIds);
}
