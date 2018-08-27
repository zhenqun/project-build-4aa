/**
 * 
 */
package com.ido85.party.sso.security.core.session;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.session.ExpiringSession;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.util.Assert;

/**
 * A {@link SessionRegistry} that retrieves session information from Spring Session, rather than maintaining it itself.
 * This allows concurrent session management with Spring Security in a clustered environment.
 * <p>
 * Relies on being able to derive the same String-based representation of the principal given to
 * {@link #getAllSessions(Object, boolean)} as used by Spring Session in order to look up the user's sessions.
 * <p>
 * Does not support {@link #getAllPrincipals()}, since that information is not available.
 *
 * @author Joris Kuipers
 * @since 1.3
 */
@Slf4j
public class SpringSessionBackedSessionRegistry implements SessionRegistry,ApplicationListener<SessionDestroyedEvent> {

	private final FindByIndexNameSessionRepository<ExpiringSession> sessionRepository;
	@SuppressWarnings("rawtypes")
	private RedisTemplate redisTemplate;
	
	public static final String ALL_PRINCIPAL_CACHE_NAME = ".ALL_PRINCIPAL";

	@SuppressWarnings("rawtypes")
	public SpringSessionBackedSessionRegistry(FindByIndexNameSessionRepository<ExpiringSession> sessionRepository) {
		Assert.notNull(sessionRepository, "sessionRepository cannot be null");
		this.sessionRepository = sessionRepository;
		this.redisTemplate = new RedisTemplate();
	}
	
	@SuppressWarnings("rawtypes")
	public SpringSessionBackedSessionRegistry(FindByIndexNameSessionRepository<ExpiringSession> sessionRepository, RedisTemplate redisTemplate) {
		Assert.notNull(sessionRepository, "sessionRepository cannot be null");
		this.sessionRepository = sessionRepository;
		this.redisTemplate = redisTemplate;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getAllPrincipals() {
//		throw new UnsupportedOperationException("SpringSessionBackedSessionRegistry does not support retrieving all principals, " +
//				"since Spring Session provides no way to obtain that information");
		return new ArrayList<Object>(redisTemplate.opsForSet().members(ALL_PRINCIPAL_CACHE_NAME));
	}

	public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
		Collection<ExpiringSession> sessions =
			this.sessionRepository.findByIndexNameAndIndexValue(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, name(principal)).values();
		List<SessionInformation> infos = new ArrayList<SessionInformation>();
		for (ExpiringSession session : sessions) {
			if (includeExpiredSessions || !Boolean.TRUE.equals(session.getAttribute(SpringSessionBackedSessionInformation.EXPIRED_ATTR))) {
				infos.add(new SpringSessionBackedSessionInformation(session, this.sessionRepository));
			}
		}
		return infos;
	}

	public SessionInformation getSessionInformation(String sessionId) {
		ExpiringSession session = this.sessionRepository.getSession(sessionId);
		if (session != null) {
			return new SpringSessionBackedSessionInformation(session, this.sessionRepository);
		}
		return null;
	}

	/*
	 * This is a no-op, as we don't administer sessions ourselves.
	 */
	public void refreshLastRequest(String sessionId) {
		
	}

	/*
	 * This is a no-op, as we don't administer sessions ourselves.
	 */
	@SuppressWarnings("unchecked")
	public void registerNewSession(String sessionId, Object principal) {
		redisTemplate.opsForSet().add(ALL_PRINCIPAL_CACHE_NAME, name(principal));
	}

	/*
	 * This is a no-op, as we don't administer sessions ourselves.
	 */
	@SuppressWarnings("unchecked")
	public void removeSessionInformation(String sessionId) {
		if(log.isDebugEnabled()){
			log.debug("Remove session information, sessionId: "+sessionId);
		}
		SpringSessionBackedSessionInformation info = (SpringSessionBackedSessionInformation)getSessionInformation(sessionId);
		if (info == null) {
			return;
		}
		if(info.getPrincipal() != null){
			redisTemplate.opsForSet().remove(ALL_PRINCIPAL_CACHE_NAME, name(info.getPrincipal()));
		}
	}

	/**
	 * Derives a String name for the given principal.
	 *
	 * @param principal as provided by Spring Security
	 * @return name of the principal, or its {@code toString()} representation if no name could be derived
	 */
	protected String name(Object principal) {
		if (principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		}
		if (principal instanceof Principal) {
			return ((Principal) principal).getName();
		}
		return principal.toString();
	}

	@Override
	public void onApplicationEvent(SessionDestroyedEvent event) {
		if(log.isDebugEnabled()){
			log.debug("Remove session information, sessionId: "+event.getId()+" source :"+event.getSource());
		}
		removeSessionInformation(event.getId());
	}
}