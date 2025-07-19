package com.devcamp.bery_real_estate.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

import java.util.concurrent.TimeUnit;

@Service
@Lazy
public class BlackListingService {

    // Tên của cache dùng để lưu trữ các JWT bị đưa vào danh sách đen
    public final static String BLACKLIST_CACHE_NAME = "jwt-black-list";

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Lấy giá trị bí mật của JWT từ file cấu hình
    @Value("${devcamp.app.jwtSecret}")
    private String jwtSecret;

    /**
     * Phương thức để đưa JWT vào danh sách đen và cài đặt thời gian tồn tại.
     * Sử dụng @CachePut để đảm bảo rằng JWT sẽ được cập nhật hoặc thêm mới vào
     * cache.
     *
     * @param jwt      JWT cần đưa vào danh sách đen
     * @param username Tên người dùng liên kết với JWT
     * @return JWT đã được đưa vào danh sách đen
     */
    @CachePut(value = BLACKLIST_CACHE_NAME, key = "#username", condition = "#result != null")
    public String blackListJwt(String jwt, String username) {
        try {
            // Kiểm tra xem JWT đã có trong danh sách đen chưa
            String existingJwt = getJwtBlackList(username);
            if (existingJwt != null && existingJwt.equals(jwt)) {
                return null; // JWT đã có trong danh sách đen, không cần cập nhật cache
            }

            // Tính toán thời gian còn lại của JWT
            long remainingTime = calculateRemainingTime(jwt, username);

            // Nếu còn thời gian sống, lưu vào Redis cache
            if (remainingTime > 0) {
                ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
                valueOps.set(username, jwt, remainingTime, TimeUnit.SECONDS);
            }

            return jwt;
        } catch (Exception e) {
            // Xử lý ngoại lệ khi thêm JWT vào danh sách đen thất bại
            System.out.println("Error adding JWT to blacklist: " + e.getMessage());
            return null;
        }
    }

    /**
     * Phương thức để lấy JWT từ cache danh sách đen (nếu tồn tại), có thay đổi dữ
     * liệu.
     * Sử dụng @Cacheable để trả về JWT từ cache nếu có, tránh việc phải truy vấn
     * lại từ nguồn dữ liệu.
     *
     * @param username Username của người dùng
     * @return JWT từ cache, null nếu không tồn tại trong cache
     */
    @Cacheable(value = BLACKLIST_CACHE_NAME, key = "#username", unless = "#result == null")
    public String getJwtBlackList(String username) {
        // Truy xuất JWT từ cache danh sách đen nếu tồn tại
        Cache.ValueWrapper valueWrapper = cacheManager.getCache(BLACKLIST_CACHE_NAME).get(username);
        return (valueWrapper != null) ? (String) valueWrapper.get() : null;
    }

    /**
     * Phương thức để tính toán thời gian còn lại của JWT token.
     *
     * @param jwtToken JWT token cần tính toán
     * @param username Username của người dùng liên kết với token
     * @return Thời gian còn lại của token trong đơn vị giây, hoặc 0 nếu token không
     *         hợp lệ hoặc đã hết hạn
     */
    @Cacheable(value = BLACKLIST_CACHE_NAME, key = "#username + '-remainingTime'", unless = "#result == null")
    public long calculateRemainingTime(String jwtToken, String username) {
        try {
            // Phân tích JWT token và lấy claims từ nó
            Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).getBody();

            // Lấy thời gian hết hạn của token (được tính bằng milliseconds từ Epoch)
            long expiryTimeMillis = claims.getExpiration().getTime();

            // Lấy thời điểm hiện tại (được tính bằng milliseconds từ Epoch)
            long currentTimeMillis = System.currentTimeMillis();

            // Tính toán thời gian sống còn lại của token (chuyển đổi thành giây)
            long remainingTimeSeconds = (expiryTimeMillis - currentTimeMillis) / 1000;

            return remainingTimeSeconds > 0 ? remainingTimeSeconds : 0;
        } catch (ExpiredJwtException e) {
            // Xử lý ngoại lệ khi token đã hết hạn
            return 0;
        } catch (Exception e) {
            // Xử lý ngoại lệ khi giải mã token thất bại
            return 0;
        }
    }

    /**
     * Phương thức để kiểm tra xem JWT có trong danh sách đen hay không.
     *
     * @param jwt JWT cần kiểm tra
     * @return true nếu JWT có trong danh sách đen, ngược lại false
     */
    public boolean isTokenBlacklisted(String jwt, String username) {
        // Sử dụng username làm key để lấy JWT từ cache
        String value = redisTemplate.opsForValue().get(username);
    
        // Kiểm tra value có phải là String và giá trị bằng jwt
        if (value != null && jwt.equals(value)) {
            return true; // JWT có trong danh sách đen
        }
    
        return false; // JWT không có trong danh sách đen
    }
    

}
