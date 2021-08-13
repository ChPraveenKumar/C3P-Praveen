package com.techm.orion.authconfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * OAuth2ResourceServerConfigJwt configures the
 * <code>@EnableResourceServer</code> class and provide the access rules and
 * paths that are protected by OAuth2 security. Applications may provide
 * multiple instances of this interface, and in general (like with other
 * Security configures), if more than one configures the same property, then
 * the last one wins.
 * 
 * @author AR115998
 *
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfigJwt extends ResourceServerConfigurerAdapter {

	private static final Logger logger = LogManager.getLogger(OAuth2ResourceServerConfigJwt.class);
	@Autowired
	private CustomAccessTokenConverter customAccessTokenConverter;
	@Value("${spring.oauth2.resourceserver.jwt-token-key}")
	private String jwtSigningKey;

	@Override
	public void configure(final HttpSecurity http) throws Exception {
		logger.info("OAuth2ResourceServerConfigJwt http-->" + http);
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and().authorizeRequests()
				.anyRequest().permitAll();
	}

	@Override
	public void configure(final ResourceServerSecurityConfigurer config) {
		logger.info("OAuth2ResourceServerConfigJwt configure-->" + config);
		config.tokenServices(tokenServices());
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setAccessTokenConverter(customAccessTokenConverter);
		converter.setSigningKey(jwtSigningKey);
		// final Resource resource = new ClassPathResource("public.txt");
		// String publicKey = null;
		// try {
		// publicKey = IOUtils.toString(resource.getInputStream());
		// } catch (final IOException e) {
		// throw new RuntimeException(e);
		// }
		// converter.setVerifierKey(publicKey);
		return converter;
	}

	@Primary
	public DefaultTokenServices tokenServices() {
		final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		return defaultTokenServices;
	}

}
