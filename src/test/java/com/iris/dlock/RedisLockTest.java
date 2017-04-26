package com.iris.dlock;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chenjinlong on 17/4/26.
 */
@Slf4j
public class RedisLockTest {

    public static JedisPool getJedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(2048);
        jedisPoolConfig.setMaxIdle(200);
        jedisPoolConfig.setNumTestsPerEvictionRun(10);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(10000);
        jedisPoolConfig.setMaxWaitMillis(1500);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setTestOnReturn(false);
        jedisPoolConfig.setJmxEnabled(true);
        jedisPoolConfig.setMinIdle(10);
        return new JedisPool(jedisPoolConfig, "tyfnet.com", 6379, 3000, "max6and7");
    }

    //    @Test
    public static void main(String[] args) {
        final JedisPool jedisPool = getJedisPool();
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            for (int i = 0; i < 10; i++) {
                executorService.execute(new Runnable() {
                    public void run() {
                        RedisLock redisLock = new RedisLock("TEST-LOCK-KEY", jedisPool);
                        try {
                            if (!redisLock.lock()) {
                                throw new RuntimeException("获取锁失败!");
                            }
                            System.out.println(Thread.currentThread().getName() + "=====>");
                            Thread.sleep(1000);
                            System.out.println(Thread.currentThread().getName() + "<=====");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            redisLock.unlock();
                        }
                    }
                });
            }

        } finally {
            executorService.shutdown();
        }

    }

    @Test
    public void redisLockTest() {
        final JedisPool jedisPool = getJedisPool();
        RedisLock redisLock = new RedisLock("TEST-LOCK-KEY", jedisPool);
        try {
            if (!redisLock.lock()) {
                throw new RuntimeException("获取锁失败!");
            }
            System.out.println(Thread.currentThread().getName() + "=====>");
            Thread.sleep(1000 * 3);
            System.out.println(Thread.currentThread().getName() + "<=====");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            redisLock.unlock();
        }
    }

    @Test
    public void multiThreadTest() {
        JedisPool jedisPool = getJedisPool();
        List<Jedis> jedisList = Lists.newArrayList();
        for (int i = 0;i<10;i++) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
            }

            System.out.println("获取redis instance");
        }

    }
}