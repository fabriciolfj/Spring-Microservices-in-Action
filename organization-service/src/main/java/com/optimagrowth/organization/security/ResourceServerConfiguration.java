package com.optimagrowth.organization.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.DELETE, "/v1/organization/**").hasRole("ADMIN")//restringindo que nesse endpoint, verbo delete, apenas usuarios com a permissão admin
                .anyRequest().authenticated(); //demais endpoints, precisa de um usuário autenticado.

    }
}