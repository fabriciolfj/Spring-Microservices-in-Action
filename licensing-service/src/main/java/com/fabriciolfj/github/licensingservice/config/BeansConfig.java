package com.fabriciolfj.github.licensingservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class BeansConfig {

    @Bean
    public LocaleResolver localeResolver() {
        var locale = new SessionLocaleResolver();
        locale.setDefaultLocale(Locale.US);
        return locale;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        var resource = new ResourceBundleMessageSource();
        resource.setUseCodeAsDefaultMessage(true);
        resource.setBasenames("messages");

        return resource;
    }

    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
