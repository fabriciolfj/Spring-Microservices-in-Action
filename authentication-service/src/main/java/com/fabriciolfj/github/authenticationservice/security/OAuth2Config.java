package com.fabriciolfj.github.authenticationservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

@Configuration
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception { //define quais clientes vão registrar no nosso serviço
        clients.inMemory()
                .withClient("ostock")
                .secret("{noop}123456")
                .authorizedGrantTypes("refresh_token", "password", "client_credentials")
                .scopes("webclient", "mobileclient"); //limites que o aplicativo pode operar ou escrever regras especificas para o escopo que o aplicativo do cliente está trabalhando
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception { //este método define os diferentes componentes utilizados no sistema de autorização. Este código está falando para o spring usar os componentes padrões
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }
}
