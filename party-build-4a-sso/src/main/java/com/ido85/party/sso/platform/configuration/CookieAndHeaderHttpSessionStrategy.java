package com.ido85.party.sso.platform.configuration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.session.Session;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionManager;
import org.springframework.session.web.http.MultiHttpSessionStrategy;

public class CookieAndHeaderHttpSessionStrategy implements MultiHttpSessionStrategy, HttpSessionManager{

	private String headerName = "x-auth-token";
	static final String DEFAULT_ALIAS = "0";
	private String sessionParam = DEFAULT_SESSION_ALIAS_PARAM_NAME;
	static final String DEFAULT_SESSION_ALIAS_PARAM_NAME = "_s";
	private static final String SESSION_IDS_WRITTEN_ATTR = CookieHttpSessionStrategy.class
			.getName().concat(".SESSIONS_WRITTEN_ATTR");
	/**
	 * The default delimiter for both serialization and deserialization.
	 */
	private static final String DEFAULT_DELIMITER = " ";
	private String serializationDelimiter = DEFAULT_DELIMITER;
	private static final Pattern ALIAS_PATTERN = Pattern.compile("^[\\w-]{1,50}$");
	private CookieSerializer cookieSerializer = new DefaultCookieSerializer();
	/**
	 * The delimiter between a session alias and a session id when reading a cookie value.
	 * The default value is " ".
	 */
	private String deserializationDelimiter = DEFAULT_DELIMITER;
	
	@Override
	public String getRequestedSessionId(HttpServletRequest request) {
		// header part
        String sessionId = request.getHeader(headerName);
//		String sessionId = request.getParameter("x-auth-token");
        System.out.println("============headerSession:"+sessionId);
        if(sessionId != null && !sessionId.isEmpty())
            return sessionId;

        // cookie part
        Map<String,String> sessionIds = getSessionIds(request);
        String sessionAlias = getCurrentSessionAlias(request);
        System.out.println("============cookieSession:"+sessionIds.get(sessionAlias));
        return sessionIds.get(sessionAlias);
	}

	@Override
	public void onNewSession(Session session, HttpServletRequest request, HttpServletResponse response) {
		// header part
        response.setHeader(headerName, session.getId());

        // cookie part
        Set<String> sessionIdsWritten = getSessionIdsWritten(request);
        if(sessionIdsWritten.contains(session.getId())) {
            return;
        }
        sessionIdsWritten.add(session.getId());

        Map<String,String> sessionIds = getSessionIds(request);
        String sessionAlias = getCurrentSessionAlias(request);
        sessionIds.put(sessionAlias, session.getId());
        Cookie sessionCookie = createSessionCookie(request, sessionIds);
        response.addCookie(sessionCookie);
	}

	private Cookie createSessionCookie(HttpServletRequest request, Map<String, String> sessionIds) {
		//cookieName是"SESSION"，spring的session cookie都是
        //以"SESSION"命名的
        Cookie sessionCookie = new Cookie("SESSION","");
        //省略部分非关键逻辑

        if(sessionIds.isEmpty()) {
            sessionCookie.setMaxAge(0);
            return sessionCookie;
        }

        if(sessionIds.size() == 1) {
            String cookieValue = sessionIds.values().iterator().next();
            sessionCookie.setValue(cookieValue);
            return sessionCookie;
        }
        StringBuffer buffer = new StringBuffer();
        for(Map.Entry<String,String> entry : sessionIds.entrySet()) {
            String alias = entry.getKey();
            String id = entry.getValue();

            buffer.append(alias);
            buffer.append(" ");
            buffer.append(id);
            buffer.append(" ");
        }
        buffer.deleteCharAt(buffer.length()-1);

        sessionCookie.setValue(buffer.toString());
        return sessionCookie;
	}

	@Override
	public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
		// header part
	    response.setHeader(headerName, "");

	    // cookie part
	    Map<String,String> sessionIds = getSessionIds(request);
	    String requestedAlias = getCurrentSessionAlias(request);
	    sessionIds.remove(requestedAlias);

	    Cookie sessionCookie = createSessionCookie(request, sessionIds);
	    response.addCookie(sessionCookie);
	}

	@Override
	public HttpServletRequest wrapRequest(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute(HttpSessionManager.class.getName(), this);
		return request;
	}

	@Override
	public HttpServletResponse wrapResponse(HttpServletRequest request, HttpServletResponse response) {
		return new MultiSessionHttpServletResponse(response, request);
	}

	@Override
	public String getCurrentSessionAlias(HttpServletRequest request) {
		if(sessionParam == null) {
            return DEFAULT_ALIAS;
        }
        String u = request.getParameter(sessionParam);
        if(u == null) {
            return DEFAULT_ALIAS;
        }
        if(!ALIAS_PATTERN.matcher(u).matches()) {
            return DEFAULT_ALIAS;
        }
        return u;
	}

	@Override
	public Map<String, String> getSessionIds(HttpServletRequest request) {
		List<String> cookieValues = this.cookieSerializer.readCookieValues(request);
		String sessionCookieValue = cookieValues.isEmpty() ? ""
				: cookieValues.iterator().next();
		Map<String, String> result = new LinkedHashMap<String, String>();
		StringTokenizer tokens = new StringTokenizer(sessionCookieValue,
				this.deserializationDelimiter);
		if (tokens.countTokens() == 1) {
			result.put(DEFAULT_ALIAS, tokens.nextToken());
			return result;
		}
		while (tokens.hasMoreTokens()) {
			String alias = tokens.nextToken();
			if (!tokens.hasMoreTokens()) {
				break;
			}
			String id = tokens.nextToken();
			result.put(alias, id);
		}
		return result;
	}

	@Override
	public String encodeURL(String url, String sessionAlias) {
		String encodedSessionAlias = urlEncode(sessionAlias);
		int queryStart = url.indexOf("?");
		boolean isDefaultAlias = DEFAULT_ALIAS.equals(encodedSessionAlias);
		if (queryStart < 0) {
			return isDefaultAlias ? url
					: url + "?" + this.sessionParam + "=" + encodedSessionAlias;
		}
		String path = url.substring(0, queryStart);
		String query = url.substring(queryStart + 1, url.length());
		String replacement = isDefaultAlias ? "" : "$1" + encodedSessionAlias;
		query = query.replaceFirst("((^|&)" + this.sessionParam + "=)([^&]+)?",
				replacement);
		if (!isDefaultAlias && url.endsWith(query)) {
			// no existing alias
			if (!(query.endsWith("&") || query.length() == 0)) {
				query += "&";
			}
			query += this.sessionParam + "=" + encodedSessionAlias;
		}

		return path + "?" + query;
	}

	@Override
	public String getNewSessionAlias(HttpServletRequest request) {
		Set<String> sessionAliases = getSessionIds(request).keySet();
		if (sessionAliases.isEmpty()) {
			return DEFAULT_ALIAS;
		}
		long lastAlias = Long.decode(DEFAULT_ALIAS);
		for (String alias : sessionAliases) {
			long selectedAlias = safeParse(alias);
			if (selectedAlias > lastAlias) {
				lastAlias = selectedAlias;
			}
		}
		return Long.toHexString(lastAlias + 1);
	}
	
	private long safeParse(String hex) {
		try {
			return Long.decode("0x" + hex);
		}
		catch (NumberFormatException notNumber) {
			return 0;
		}
	}
	
	private String urlEncode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * A {@link CookieHttpSessionStrategy} aware {@link HttpServletResponseWrapper}.
	 */
	class MultiSessionHttpServletResponse extends HttpServletResponseWrapper {
		private final HttpServletRequest request;

		MultiSessionHttpServletResponse(HttpServletResponse response,
				HttpServletRequest request) {
			super(response);
			this.request = request;
		}

		@Override
		public String encodeRedirectURL(String url) {
			url = super.encodeRedirectURL(url);
			return CookieAndHeaderHttpSessionStrategy.this.encodeURL(url,
					getCurrentSessionAlias(this.request));
		}

		@Override
		public String encodeURL(String url) {
			url = super.encodeURL(url);

			String alias = getCurrentSessionAlias(this.request);
			return CookieAndHeaderHttpSessionStrategy.this.encodeURL(url, alias);
		}
	}

	@SuppressWarnings("unchecked")
	private Set<String> getSessionIdsWritten(HttpServletRequest request) {
		Set<String> sessionsWritten = (Set<String>) request
				.getAttribute(SESSION_IDS_WRITTEN_ATTR);
		if (sessionsWritten == null) {
			sessionsWritten = new HashSet<String>();
			request.setAttribute(SESSION_IDS_WRITTEN_ATTR, sessionsWritten);
		}
		return sessionsWritten;
	}
	
//	private String createSessionCookieValue(Map<String, String> sessionIds) {
//		if (sessionIds.isEmpty()) {
//			return "";
//		}
//		if (sessionIds.size() == 1 && sessionIds.keySet().contains(DEFAULT_ALIAS)) {
//			return sessionIds.values().iterator().next();
//		}
//
//		StringBuffer buffer = new StringBuffer();
//		for (Map.Entry<String, String> entry : sessionIds.entrySet()) {
//			String alias = entry.getKey();
//			String id = entry.getValue();
//
//			buffer.append(alias);
//			buffer.append(this.serializationDelimiter);
//			buffer.append(id);
//			buffer.append(this.serializationDelimiter);
//		}
//		buffer.deleteCharAt(buffer.length() - 1);
//		return buffer.toString();
//	}
}
