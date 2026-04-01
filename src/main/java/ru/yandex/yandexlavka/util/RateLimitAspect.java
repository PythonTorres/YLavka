package ru.yandex.yandexlavka.util;

import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Aspect
public class RateLimitAspect {
    private final ConcurrentHashMap<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

    @Around("@annotation(RateLimited)")
    public Object applyRateLimit(ProceedingJoinPoint joinPoint) throws Throwable{
        String endpoint = joinPoint.getSignature().toShortString();
        RateLimiter rateLimiter = rateLimiters.computeIfAbsent(endpoint, k -> RateLimiter.create(10.0));

        if (rateLimiter.tryAcquire()) {
            return joinPoint.proceed();
        } else {
            return new ResponseEntity(HttpStatus.TOO_MANY_REQUESTS);
        }
    }
}
