package com.fabriciolfj.github.licensingservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

/*@Configuration
@EnableCaching
public class RedisConfigCluster extends CachingConfigurerSupport {

    @Value("${redis.master.hostname}")
    private String masterHostName;

    @Value("${redis.replica.hostname}")
    private String replicaHostName;

    @Value("${redis.master.port}")
    private int masterPort;

    @Value("${redis.replica.port}")
    private int replicaPort;

    @Value("${redis.prefix}")
    private String redisPrefix;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        final RedisStaticMasterReplicaConfiguration elasticCache = new RedisStaticMasterReplicaConfiguration(masterHostName, masterPort);
        elasticCache.addNode(replicaHostName, replicaPort);

        final LettuceClientConfiguration clientConfig = LettuceClientConfiguration
                .builder()
                .commandTimeout(Duration.ofMinutes(1))
                .readFrom(ReadFrom.MASTER_PREFERRED)
                .useSsl()
                .build();

        return new LettuceConnectionFactory(elasticCache, clientConfig);
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(final RedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean(name = "cacheManager15Minutes")
    public CacheManager cacheManager15Minutes(RedisConnectionFactory redisConnectionFactory) {
        final Duration expiration = Duration.ofMinutes(15);
        return this.getRedisCacheManager(redisConnectionFactory, expiration);
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErroHandler();
    }

    private RedisCacheManager getRedisCacheManager(final RedisConnectionFactory redisConnectionFactory, final Duration expiration) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith(redisPrefix)
                .entryTtl(expiration))
                .build();
    }
}*/
