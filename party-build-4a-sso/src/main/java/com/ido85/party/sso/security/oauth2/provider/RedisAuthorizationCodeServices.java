package com.ido85.party.sso.security.oauth2.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

import javax.inject.Named;

/**
 * Created by Administrator on 2017/6/30.
 */
@Named("redisCodeServices")
public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

    @Autowired
    @Qualifier("objectRedisTemplate")
    private RedisTemplate objectRedisTemplate;

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        objectRedisTemplate.opsForValue().set(code, authentication);
    }

    @Override
    protected OAuth2Authentication remove(String code) {
        OAuth2Authentication auth2Authentication = (OAuth2Authentication)objectRedisTemplate.opsForValue().get(code);
        objectRedisTemplate.delete(code);
        return auth2Authentication;
    }
}
