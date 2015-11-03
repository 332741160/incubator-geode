package com.vmware.gemfire.tools.pulse.internal.security;

import java.util.Collection;

import javax.management.remote.JMXConnector;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import com.vmware.gemfire.tools.pulse.internal.data.Repository;
import com.vmware.gemfire.tools.pulse.internal.log.PulseLogWriter;

/**
 * Spring security AuthenticationProvider for GemFire. It connects to 
 * gemfire manager using given credentials. Successful connect is treated
 * as successful authentication and web user is authenticated
 *
 * @author Tushar Khairnar
 * @since version 9.0
 */
public class GemFireAuthenticationProvider implements AuthenticationProvider {
	
  private final static PulseLogWriter LOGGER = PulseLogWriter.getLogger();	

	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
			  
		if (authentication instanceof GemFireAuthentication) {
			GemFireAuthentication gemAuth = (GemFireAuthentication) authentication;
			LOGGER.fine("GemAuthentication is connected? = "
					+ gemAuth.getJmxc());
			if(gemAuth.getJmxc()!=null && gemAuth.isAuthenticated())
				return gemAuth;
		}
		
		String name = authentication.getName();
		String password = authentication.getCredentials().toString();

		try {
		  LOGGER.fine("Connecting to GemFire with user=" + name);
		  JMXConnector jmxc = Repository.get().getCluster().connectToGemFire(name, password);
		  if(jmxc!=null) {
  			Collection<GrantedAuthority> list = GemFireAuthentication.populateAuthorities(jmxc);
  			GemFireAuthentication auth = new GemFireAuthentication(
  					authentication.getPrincipal(),
  					authentication.getCredentials(), list, jmxc);
  			LOGGER.fine("For user " + name + " authList="+ list);
  			return auth;
		  } else 
		    throw new AuthenticationServiceException("JMX Connection unavailable");
		} catch (Exception e) {
		  throw new BadCredentialsException("Error connecting to GemFire JMX Server", e);			
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}	

}
