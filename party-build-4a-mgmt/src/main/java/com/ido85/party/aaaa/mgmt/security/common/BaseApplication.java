package com.ido85.party.aaaa.mgmt.security.common;

import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * application 基类
 * @author fire
 *
 */
public abstract class BaseApplication {
	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@PersistenceContext(unitName="admin")
	protected EntityManager entity;
	@PersistenceContext(unitName="internet")
	protected EntityManager internetEntity;
	@PersistenceContext(unitName="business")
	protected EntityManager businessEntity;

	@Transactional
	public boolean updateEntitySys(String sql, Map<String, Object> params) throws Exception {
		Query query = entity.createQuery(sql);
		
		if(null != params && params.size() > 0){
			Set<String> set = params.keySet();
				for (String str : set) {
					query.setParameter(str, params.get(str));
				}
		}
		return query.executeUpdate() >= 0?true:false;
	}
	
	@SuppressWarnings("unchecked")
	public <E> List<E> queryEntityNodes(String sql, Map<String, Object> params, Class<E> e) throws Exception {
		Query query = entity.createQuery(sql, e);
		
		if(null != params && params.size() > 0){
			Set<String> set = params.keySet();
			for (String str : set) {
				query.setParameter(str, params.get(str));
			}
		}
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public <E> List<E> queryPageEntityNodes(String sql, Map<String, Object> params,
			Integer pageSize, Integer pageNo, Class<E> e) throws Exception {
		Query query = entity.createQuery(sql, e);
		
		if(null != params && params.size() > 0){
			Set<String> set = params.keySet();
			for (String str : set) {
				query.setParameter(str, params.get(str));
			}
		}
		query.setFirstResult(pageSize * (pageNo - 1));
		query.setMaxResults(pageSize);
		return query.getResultList();
	}
	
	public Long queryPageEntityCount(String sql, Map<String, Object> params) throws Exception {
		Query query = entity.createQuery(sql);
		
		if(null != params && params.size() > 0){
			Set<String> set = params.keySet();
			for (String str : set) {
				query.setParameter(str, params.get(str));
			}
		}
		return StringUtils.toLong(query.getSingleResult());
	}
	
	@SuppressWarnings("unchecked")
	public <E> List<E> queryEntityNode(String sql, Map<String, Object> params, Class<E> e) throws Exception {
		Query query = entity.createQuery(sql, e);
		
		if(null != params && params.size() > 0){
			Set<String> set = params.keySet();
			for (String str : set) {
				query.setParameter(str, params.get(str));
			}
		}
		return query.getResultList();
	}


	@SuppressWarnings("unchecked")
	public <E> List<E> queryPageEntityNodesForInternet(String sql, Map<String, Object> params,
											Integer pageSize, Integer pageNo, Class<E> e) throws Exception {
		Query query = internetEntity.createQuery(sql, e);

		if(null != params && params.size() > 0){
			Set<String> set = params.keySet();
			for (String str : set) {
				query.setParameter(str, params.get(str));
			}
		}
		query.setFirstResult(pageSize * (pageNo - 1));
		query.setMaxResults(pageSize);
		return query.getResultList();
	}

	public Long queryPageEntityCountForInternet(String sql, Map<String, Object> params) throws Exception {
		Query query = internetEntity.createQuery(sql);

		if(null != params && params.size() > 0){
			Set<String> set = params.keySet();
			for (String str : set) {
				query.setParameter(str, params.get(str));
			}
		}
		return StringUtils.toLong(query.getSingleResult());
	}


	@SuppressWarnings("unchecked")
	public <E> List<E> queryPageEntityNodesForBusiness(String sql, Map<String, Object> params,
											Integer pageSize, Integer pageNo, Class<E> e) throws Exception {
		Query query = businessEntity.createQuery(sql, e);

		if(null != params && params.size() > 0){
			Set<String> set = params.keySet();
			for (String str : set) {
				query.setParameter(str, params.get(str));
			}
		}
		query.setFirstResult(pageSize * (pageNo - 1));
		query.setMaxResults(pageSize);
		return query.getResultList();
	}

	public Long queryPageEntityCountForBusiness(String sql, Map<String, Object> params) throws Exception {
		Query query = businessEntity.createQuery(sql);

		if(null != params && params.size() > 0){
			Set<String> set = params.keySet();
			for (String str : set) {
				query.setParameter(str, params.get(str));
			}
		}
		return StringUtils.toLong(query.getSingleResult());
	}

	public <E> List<E> queryEntityList(String sql, Map<String, Object> params, Class<E> e) throws Exception {
		Query query = businessEntity.createQuery(sql, e);

		if(null != params && params.size() > 0){
			Set<String> set = params.keySet();
			for (String str : set) {
				query.setParameter(str, params.get(str));
			}
		}
		return query.getResultList();
	}



}
