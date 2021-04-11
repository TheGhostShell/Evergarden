package com.evergarden.cms.app.config.caching;

import com.evergarden.cms.context.user.domain.entity.Token;
import com.evergarden.cms.context.user.domain.entity.User;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    private javax.cache.CacheManager ehcacheManager() {
        CachingProvider provider = Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
        return provider.getCacheManager();
    }

    @Override
    @Bean
    public CacheManager cacheManager() {
        return new JCacheCacheManager(ehcacheManager());
    }

    @Bean
    public Cache<String, Token> tokenCache() {
        MutableConfiguration<String, Token>  configuration = new MutableConfiguration<String, Token>()
            .setTypes(String.class, Token.class)
            .setStoreByValue(false)
            .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.MINUTES, 359)));
        return  ehcacheManager().createCache("tokenCache", configuration);
    }

    @Bean
    public Cache<String, User> userCache() {
        MutableConfiguration<String, User>  configuration = new MutableConfiguration<String, User>()
            .setTypes(String.class, User.class)
            .setStoreByValue(false)
            .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.MINUTES, 5)));
        return  ehcacheManager().createCache("userCache", configuration);
    }
}

