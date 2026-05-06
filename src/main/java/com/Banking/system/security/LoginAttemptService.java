package com.Banking.system.security;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int max_attempts = 5;
    private static final int lock_duration_mins =15;

    private final Map<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    public void loginSucceeded(String email) {
        attempts.remove(normalizeEmail(email));
    }

    public void loginFailed(String email) {
        attempts.compute(normalizeEmail(email), (key, existingRecord) -> {
            AttemptRecord record = existingRecord == null ? new AttemptRecord() : existingRecord;
            record.increment();
            return record;
        });
    }

    public boolean isBlocked(String email) {
        String normalizedEmail = normalizeEmail(email);
        AttemptRecord record = attempts.get(normalizedEmail);
        if (record == null) return false;

        if (record.isLocked() && record.getLockTime().plusMinutes(lock_duration_mins).isBefore(LocalDateTime.now())) {
            attempts.remove(normalizedEmail);
            return false;
        }

        return record.isLocked();
    }

    public int getRemainingAttempts(String email) {
        AttemptRecord record = attempts.get(normalizeEmail(email));
        if (record == null) return max_attempts;
        return Math.max(0, max_attempts - record.getCount());
    }

    public LocalDateTime getLockExpiryTime(String email) {
        AttemptRecord record = attempts.get(normalizeEmail(email));
        if (record == null || !record.isLocked()) return null;
        return record.getLockTime().plusMinutes(lock_duration_mins);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }




    private static class AttemptRecord {
        private int count = 0;
        private LocalDateTime lockTime;

        public void increment() {
            count++;
            if (count >= max_attempts) {
                lockTime = LocalDateTime.now();
            }
        }

        public boolean isLocked()        { return count >= max_attempts; }
        public int getCount()            { return count; }
        public LocalDateTime getLockTime() { return lockTime; }
    }
}
