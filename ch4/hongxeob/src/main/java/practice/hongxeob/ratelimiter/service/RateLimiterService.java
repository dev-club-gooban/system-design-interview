package practice.hongxeob.ratelimiter.service;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimiterService {
    @RateLimiter(name = "myRateLimitedService", fallbackMethod = "rateLimitFallback")
    public String rateLimit() {
        return "hello";
    }

    public String rateLimitFallback(Throwable t) {
        return "Rate limit exceeded. Please try again later.";
    }
}
