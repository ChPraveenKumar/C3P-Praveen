package com.techm.c3p.core.authconfig;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

/**
 * OAuth2.0 client configuration base class. The configuration of OAuth2.0 will
 * be maintained in application.properites. This class support multiple OAuth2.0
 * client configuration by specifying the OAuth2.0 configuration of
 * access-token-uri, client-id, client-secret, client.grantType, scopes
 * 
 * OAuth2RestTemplate can be defined like,  
 * Bean(name="pythonOauth2RestTemplate") Bean(name="javaOauth2RestTemplate")
 * 
 * @author AR115998
 *
 */
@Configuration
@EnableOAuth2Client
public class OAuth2ClientConfig extends WebSecurityConfigurerAdapter {
	private static final Logger logger = LogManager.getLogger(OAuth2ClientConfig.class);

	@Value("${python.oauth2.client.access-token-uri}")
	private String tokenUrl;

	@Value("${python.oauth2.client.client-id}")
	private String clientId;

	@Value("${python.oauth2.client.client-secret}")
	private String clientSecret;

	@Value("${python.oauth2.client.grantType}")
	private String grantType;

	@Value("${python.user.name}")
	private String username;

	@Value("${python.user.password}")
	private String password;

	@Value("#{'${python.oauth2.client.scopes}'.split(',')}")
	private List<String> scopes;

	@Override
	public void configure(final HttpSecurity http) throws Exception {
		logger.info("OAuth2ClientConfig http-->" + http);
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and().authorizeRequests()
				.anyRequest().permitAll();
	}

	@Bean
	protected OAuth2ProtectedResourceDetails resource() {
		ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();

		resource.setAccessTokenUri(tokenUrl);
		resource.setClientId(clientId);
		resource.setClientSecret(clientSecret);
		resource.setUsername(username);
		resource.setPassword(password);
		resource.setGrantType(grantType);
		logger.info("resource --> scopesI-" + scopes);
		resource.setScope(scopes);
		logger.info("resource --> resource Access URI-" + resource.getAccessTokenUri());
		return resource;
	}

	@Bean(name = "pythonOauth2RestTemplate")
	public OAuth2RestTemplate pythonOauth2RestTemplate() {
		OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(resource(), new DefaultOAuth2ClientContext());
		oAuth2RestTemplate.setAccessTokenProvider(new ResourceOwnerPasswordAccessTokenProvider());
		logger.info("pythonOauth2RestTemplate --> token -" + oAuth2RestTemplate.getAccessToken());
		return oAuth2RestTemplate;
	}

}
