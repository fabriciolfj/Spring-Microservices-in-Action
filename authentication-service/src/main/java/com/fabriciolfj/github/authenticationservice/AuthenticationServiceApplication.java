package com.fabriciolfj.github.authenticationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
@EnableResourceServer //falar para o microserviço que é um recurso protegido
@EnableAuthorizationServer // falar para o spring cloud que esse serviço funcionará como um serviço OAuth2, adicionando vários endpoints correspondentes
public class AuthenticationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServiceApplication.class, args);
	}
	
	@RequestMapping(value = { "/user"}, produces = "application/json")
	public Map<String, Object> user(final OAuth2Authentication user) {
		final var userInfo = new HashMap<String, Object>();
		userInfo.put("user", user.getUserAuthentication().getPrincipal());
		userInfo.put("authorities", AuthorityUtils.authorityListToSet(user.getUserAuthentication().getAuthorities()));

		return userInfo;
	}

}
