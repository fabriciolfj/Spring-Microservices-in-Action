package com.fabriciolfj.github.licensingservice;

import com.fabriciolfj.github.licensingservice.security.ServiceConfig;
import com.fabriciolfj.github.licensingservice.service.client.CustomChannels;
import com.fabriciolfj.github.licensingservice.service.client.OrganizationFeignClient;
import com.fabriciolfj.github.licensingservice.utils.UserContextInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Slf4j
@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = {OrganizationFeignClient.class})
@SpringBootApplication
@RefreshScope
@EnableResourceServer
@EnableBinding(CustomChannels.class)
public class LicensingServiceApplication {

	@Autowired
	private ServiceConfig service;

	public static void main(String[] args) {
		SpringApplication.run(LicensingServiceApplication.class, args);
	}

	//não propaga tokens jwt
	/*@Bean //bean para lidar com propagacao de token
	public OAuth2RestTemplate oAuth2RestTemplate(final OAuth2ClientContext oauth2ClientContext, final OAuth2ProtectedResourceDetails details) {
		return new OAuth2RestTemplate(details, oauth2ClientContext);
	}*/

	@Primary
	@Bean
	//@LoadBalanced
	public RestTemplate getCustomRestTemplate() {
		var template = new RestTemplate();
		var interceptors = template.getInterceptors();

		if (interceptors == null) {
			template.setInterceptors(Collections.singletonList(new UserContextInterceptor())); //injetara um cabeçalho de autorização, executa antes que uma chamada baseada em rest seja feita
		} else {
			interceptors.add(new UserContextInterceptor());
			template.setInterceptors(interceptors);
		}

		return template;
	}

	/*@StreamListener(Sink.INPUT)
	public void loggerSink(final OrganizationChangeModel model) {
		log.info("Received an {} event for organization id {}", model.getAction(), model.getOrganizationId());
	}*/
}
