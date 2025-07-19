package com.devcamp.bery_real_estate.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class CacheConfig {

    // Tên của cache dùng để lưu trữ các JWT bị đưa vào danh sách đen
    public final static String BLACKLIST_CACHE_NAME = "jwt-black-list";

    // Đọc giá trị của spring.redis.host từ application.properties, mặc định là localhost
    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    // Đọc giá trị của spring.redis.port từ application.properties, mặc định là 6379
    @Value("${spring.redis.port:6379}")
    private int redisPort;

    // Đọc giá trị của jwtExpirationMs từ application.properties
    @Value("${devcamp.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    // Lettuce connection factory
    private final LettuceConnectionFactory lettuceConnectionFactory;

    public CacheConfig(@Lazy LettuceConnectionFactory lettuceConnectionFactory) {
        this.lettuceConnectionFactory = lettuceConnectionFactory;
    }

    // Bean để tạo Lettuce connection factory
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisHost, redisPort));
    }

    // Bean để tùy chỉnh cấu hình Redis cache manager
    @Bean
    RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> {
            Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
            // Đặt cấu hình mặc định cho cache danh sách đen với TTL dựa trên jwtExpirationMs
            configurationMap.put(BLACKLIST_CACHE_NAME,
                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMillis(jwtExpirationMs)));
            builder.withInitialCacheConfigurations(configurationMap);
        };
    }

    // Định nghĩa một RedisCacheManager
    @Bean
    public RedisCacheManager cacheManager() {
        // Sử dụng builder để tạo RedisCacheManager
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(lettuceConnectionFactory);

        // Cấu hình mặc định cho Redis Cache Manager
        return builder.build();
    }

    // Định nghĩa một RedisTemplate để tương tác với Redis
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        // Cấu hình Serializer và Deserializer nếu cần
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // Serializer cho khóa là String
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // Serializer cho giá trị là JSON

        redisTemplate.setHashKeySerializer(new StringRedisSerializer()); // Serializer cho khóa trong hash là String
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer()); // Serializer cho giá trị trong hash là JSON

        redisTemplate.afterPropertiesSet(); // Đảm bảo các thuộc tính đã được thiết lập
        return redisTemplate;
    }
}
