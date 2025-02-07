package practice.hongxeob.ratelimiter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.hongxeob.ratelimiter.service.RateLimiterService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class RateLimiterController {
    private final RateLimiterService rateLimiterService;

    @GetMapping("rate-limit")
    public String rateLimit() {
        return rateLimiterService.rateLimit();
    }
}
