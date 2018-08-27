package com.ido85.party.sso.security.authentication.provider;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.Assert;

import com.ido85.party.sso.security.exceptions.CaLoginValidateFailException;
import com.ido85.party.sso.security.exceptions.CaptchaValidateException;
import com.ido85.party.sso.security.utils.StringUtils;

public class DefaultDaoAuthenticationProvider extends DefaultAbstractUserDetailsAuthenticationProvider {
	// ~ Static fields/initializers
		// =====================================================================================

		/**
		 * The plaintext password used to perform
		 * {@link PasswordEncoder#isPasswordValid(String, String, Object)} on when the user is
		 * not found to avoid SEC-2056.
		 */
		private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

		// ~ Instance fields
		// ================================================================================================

		private PasswordEncoder passwordEncoder;

		/**
		 * The password used to perform
		 * {@link PasswordEncoder#isPasswordValid(String, String, Object)} on when the user is
		 * not found to avoid SEC-2056. This is necessary, because some
		 * {@link PasswordEncoder} implementations will short circuit if the password is not
		 * in a valid format.
		 */
		private String userNotFoundEncodedPassword;

		private UserDetailsService userDetailsService;

		public DefaultDaoAuthenticationProvider() {
			setPasswordEncoder(new StandardPasswordEncoder());
		}

		// ~ Methods
		// ========================================================================================================

		protected void additionalAuthenticationChecks(UserDetails userDetails,
				DefaultUsernamePasswordAuthenticationToken authentication)
				throws AuthenticationException {

			if (authentication.getCredentials() == null) {
				logger.debug("Authentication failed: no credentials provided");

				throw new BadCredentialsException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.badCredentials",
						"Bad credentials"));
			}

			String presentedPassword = authentication.getCredentials().toString();

			if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
				logger.debug("Authentication failed: password does not match stored value");

				throw new BadCredentialsException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.badCredentials",
						"Bad credentials"));
			}
		}

		protected void doAfterPropertiesSet() throws Exception {
			Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
		}

		protected final UserDetails retrieveUser(String username,
				DefaultUsernamePasswordAuthenticationToken authentication)
				throws AuthenticationException {
			UserDetails loadedUser;
			
			try {
				loadedUser = this.getUserDetailsService().loadUserByUsername(username);
			}
			catch (UsernameNotFoundException notFound) {
				if (authentication.getCredentials() != null) {
					String presentedPassword = authentication.getCredentials().toString();
					passwordEncoder.matches(presentedPassword, userNotFoundEncodedPassword);
				}
				throw notFound;
			}
			catch (Exception repositoryProblem) {
				throw new InternalAuthenticationServiceException(
						repositoryProblem.getMessage(), repositoryProblem);
			}

			if (loadedUser == null) {
				throw new InternalAuthenticationServiceException(
						"UserDetailsService returned null, which is an interface contract violation");
			}
			return loadedUser;
		}

		public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
			Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");

			this.userNotFoundEncodedPassword = passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
			this.passwordEncoder = passwordEncoder;
		}

		protected PasswordEncoder getPasswordEncoder() {
			return passwordEncoder;
		}


		public void setUserDetailsService(UserDetailsService userDetailsService) {
			this.userDetailsService = userDetailsService;
		}

		protected UserDetailsService getUserDetailsService() {
			return userDetailsService;
		}
}
