package com.fabriciolfj.github.licensingservice.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/v1/organization/**")
                .hasRole("ADMIN")
                .antMatchers("/actuator/**").permitAll()
                .anyRequest()
                .authenticated();
    }
}
