package com.iris.interceptor;


import com.iris.annotations.Dlock;
import com.iris.annotations.LockKey;
import com.iris.dlock.RedisLock;
import lombok.Getter;
import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPool;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by chenjinlong on 17/4/26.
 */
public class DLockInterceptor implements MethodInterceptor {

    @Getter
    @Setter
    JedisPool jedisPool;

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Method method = methodInvocation.getMethod();
        Dlock dlock = method.getAnnotation(Dlock.class);
        String key = dlock.value();

        Object[] paramValues = methodInvocation.getArguments();
        Annotation[][] paramsAnnotations = method.getParameterAnnotations();

        for (int i = 0;i<paramValues.length;i++) {
            Object candidateKey = paramValues[i];
            Annotation[] paramAnnotations = paramsAnnotations[i];
            for (Annotation paramAnnotation : paramAnnotations) {
                if (LockKey.class.equals(paramAnnotation.annotationType())) {
                    key = String.valueOf(candidateKey);
                    break;
                }
            }
        }
        if (StringUtils.isEmpty(key)) {
            throw new RuntimeException("δָ����key!");
        }
        RedisLock redisLock = new RedisLock(key, jedisPool);

        try {
            if (!redisLock.lock()) {
                throw new RuntimeException("��ȡ��ʧ��!");
            }
            return methodInvocation.proceed();
        } finally {
            redisLock.unlock();
        }

    }
}