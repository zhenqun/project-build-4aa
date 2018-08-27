/**
 * 
 */
package com.ido85.party.aaaa.mgmt.security.dbsync.events;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventOperLog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String operName;
	
}
