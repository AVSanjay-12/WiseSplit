package com.sanjay.splitwise.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Integer> redisTemplate;

    public RateLimitFilter(RedisTemplate<String, Integer> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        boolean shouldLimit = path.equals("/auth/login") || path.equals("/users");

        if (!shouldLimit) {
            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String ip = request.getRemoteAddr();

        if (isRateLimited(ip)) {

            response.setStatus(429);
            response.setContentType("application/json");

            new ObjectMapper().writeValue(
                    response.getOutputStream(),
                    Map.of(
                            "status", 429,
                            "error", "Too Many Requests",
                            "message",
                            "Rate limit exceeded"
                    )
            );

            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimited(String ip) {

        String key = "rate_limit:" + ip;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {

            redisTemplate.expire(key, Duration.ofMinutes(1));
        }

        return count != null && count > 5;
    }
}