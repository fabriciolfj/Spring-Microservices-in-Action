package com.fabriciolfj.github.authenticationservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class JWTTokenStoreConfig {

    @Autowired
    private ServiceConfig serviceConfig;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    @Primary //se houver mais de um bean, use este
    public DefaultTokenServices tokenServices() {
        var defaultToken = new DefaultTokenServices(); //usado para ler dados para um token
        defaultToken.setTokenStore(tokenStore());
        defaultToken.setSupportRefreshToken(true);
        return defaultToken;
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() { //usado para converter, como o token será traduzido
        var converter = new JwtAccessTokenConverter();
        converter.setSigningKey(serviceConfig.getJwtSigningKey()); //definição da chave para assinatura
        return converter;
    }

    @Bean
    public TokenEnhancer jwtTokenEnhancer() { //estratégia customizada para adicionar mais dados ao token
        return new JWTTokenEnhancer();
    }
}
