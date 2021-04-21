package com.fabriciolfj.github.licensingservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

@Slf4j
public class CustomCacheErroHandler implements CacheErrorHandler {
    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
      log.error(e.getMessage());
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {

    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {

    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {

    }
}
