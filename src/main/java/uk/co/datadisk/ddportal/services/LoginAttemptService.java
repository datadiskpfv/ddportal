package uk.co.datadisk.ddportal.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptService {

    Logger logger = LoggerFactory.getLogger(getClass());

    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    private static final int ATTEMPT_INCREMENT = 1;

    private LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptService() {
        super();

        loginAttemptCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(15, MINUTES)
                .maximumSize(100)                                   // Maximum entries
                .build(new CacheLoader<String, Integer>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    public void addUserToLoginAttemptCache(String username)  {

        // All new users will automatically start from 0
        int attempts = 0;

        try {
            attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username).intValue();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        logger.info("Login Attempts for username: " + username + " Attempts: " + attempts);
        loginAttemptCache.put(username, attempts);
    }

    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
