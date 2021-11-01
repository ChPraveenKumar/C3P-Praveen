package com.techm.c3p.core.authconfig;

import java.util.Map;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;

/**
 * Customized Converter for token service implementations that store
 * authentication data inside the token in OAuth2.0
 * 
 * @author AR115998
 *
 */
@Component
public class CustomAccessTokenConverter extends DefaultAccessTokenConverter {

	@Override
	public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
		OAuth2Authentication authentication = super.extractAuthentication(claims);
		authentication.setDetails(claims);
		return authentication;
	}

}
