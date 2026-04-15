package com.example.rentflat.service;

import com.example.rentflat.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final long OTP_TTL_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;
    private static final long ATTEMPT_WINDOW_MINUTES = 15;

    private final StringRedisTemplate redis;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates and stores OTP for the given phone number.
     * Rate-limited: max 5 requests per phone per 15 minutes.
     *
     * @return the generated OTP (dev use only)
     */
    public String generateAndStore(String phone) {
        checkRateLimit(phone);

        String otp = String.format("%0" + OTP_LENGTH + "d",
                secureRandom.nextInt((int) Math.pow(10, OTP_LENGTH)));

        redis.opsForValue().set(otpKey(phone), otp, OTP_TTL_MINUTES, TimeUnit.MINUTES);
        incrementAttempts(phone);

        // TODO: replace with real SMS gateway call in production
        log.info("[DEV] OTP for {}: {}", phone, otp);
        return otp;
    }

    /**
     * Verifies the OTP. Deletes it from Redis on success.
     */
    public void verify(String phone, String otp) {
        String stored = redis.opsForValue().get(otpKey(phone));
        if (stored == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "OTP expired or not requested");
        }
        if (!stored.equals(otp)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }
        redis.delete(otpKey(phone));
    }

    // ── Internals ──────────────────────────────────────────────────────────────

    private void checkRateLimit(String phone) {
        String value = redis.opsForValue().get(attemptsKey(phone));
        if (value != null && Integer.parseInt(value) >= MAX_ATTEMPTS) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS,
                    "Too many OTP requests. Please wait 15 minutes.");
        }
    }

    private void incrementAttempts(String phone) {
        String key = attemptsKey(phone);
        Long count = redis.opsForValue().increment(key);
        if (count != null && count == 1) {
            redis.expire(key, ATTEMPT_WINDOW_MINUTES, TimeUnit.MINUTES);
        }
    }

    private static String otpKey(String phone)      { return "otp:" + phone; }
    private static String attemptsKey(String phone) { return "otp:attempts:" + phone; }
}
