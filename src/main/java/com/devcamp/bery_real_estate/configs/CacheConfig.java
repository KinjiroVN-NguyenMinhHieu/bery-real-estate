package com.devcamp.bery_real_estate.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisPassword;
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
    public static final String BLACKLIST_CACHE_NAME = "jwt-black-list";

    // Đọc giá trị của spring.redis.host từ application.properties, mặc định là localhost
    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    // Đọc giá trị của spring.redis.port từ application.properties, mặc định là 6379
    @Value("${spring.redis.port:6379}")
    private int redisPort;

    // Đọc giá trị của spring.redis.password từ application.properties, mặc định là rỗng
    @Value("${spring.redis.password:}")
    private String redisPassword;

    // Đọc giá trị của jwtExpirationMs từ application.properties
    @Value("${devcamp.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    /**
     * Bean để tạo Lettuce connection factory.
     * Cần cấu hình cả password và SSL nếu dùng Redis Cloud.
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        if (redisPassword != null && !redisPassword.isBlank()) {
            config.setPassword(RedisPassword.of(redisPassword));
        }
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        // Nếu dùng Redis Cloud có hỗ trợ SSL (redis cli có dạng rediss://...), cần bật dòng này.
        // factory.setUseSsl(true);
        return factory;
    }

    /**
     * Bean để tùy chỉnh cấu hình Redis cache manager.
     * Thiết lập TTL riêng cho từng cache nếu muốn.
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> {
            Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();

            // Đặt cấu hình mặc định cho cache danh sách đen với TTL dựa trên jwtExpirationMs
            configurationMap.put(BLACKLIST_CACHE_NAME,
                    RedisCacheConfiguration.defaultCacheConfig()
                            .entryTtl(Duration.ofMillis(jwtExpirationMs)));

            builder.withInitialCacheConfigurations(configurationMap);
        };
    }

    /**
     * Định nghĩa một RedisCacheManager từ LettuceConnectionFactory.
     */
    @Bean
    public RedisCacheManager cacheManager(LettuceConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory).build();
    }

    /**
     * Định nghĩa một RedisTemplate để tương tác với Redis.
     * Thiết lập serializer cho key, value và hash key/value.
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // Serializer cho khóa là String
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // Serializer cho giá trị là JSON
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // Serializer cho khóa và giá trị trong Hash
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        // Đảm bảo các thuộc tính đã được thiết lập
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
