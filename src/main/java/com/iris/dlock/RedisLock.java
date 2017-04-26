package com.iris.dlock;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by chenjinlong on 17/4/26.
 */
@Slf4j
public class RedisLock {

    private final JedisPool jedisPool;

    private final String lockKey;

    //客户端获取锁的超时时间10秒=10*1000*1000*1000纳秒
    private long timeoutTime = 10*1000*1000*1000l;

    //客户端获占用锁的最长时间为60秒=60*1000*1000*1000纳秒
    private long expireTime = 60*1000*1000*1000l;

    private Boolean locked = false;

    public RedisLock(String lockKey,JedisPool jedisPool) {
        this.lockKey  = lockKey;
        this.jedisPool = jedisPool;
    }

    public synchronized Boolean lock() throws InterruptedException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            long timeout = timeoutTime;
            log.debug("正在尝试获得锁:lockKey={}...",this.lockKey);
            while (timeout>0) {
                long currentTime = System.nanoTime();
                long expires = currentTime+expireTime;
                if (jedis.setnx(this.lockKey,String.valueOf(expires)) == 1) {
                    log.debug("成功获得锁:lockKey={}",this.lockKey);
                    return setLock();
                }
                log.debug("锁已被占领,正在尝试是否超时:lockKey={}...", this.lockKey);
                String oldLockValue = jedis.get(this.lockKey);
//                log.debug("锁超时时间expires={},当前时间currentTime={}",oldLockValue,currentTime);
                if (oldLockValue != null && Long.parseLong(oldLockValue)<currentTime) {
                    log.debug("锁超时,正在尝试获取超时的锁:lockKey={}...", this.lockKey);
                    String newLockValue = jedis.getSet(this.lockKey, String.valueOf(expires));
                    if (newLockValue != null && newLockValue.equals(oldLockValue)) {
                        log.debug("已获取超时的锁:lockKey={}",this.lockKey);
                        return setLock();
                    }
                }
                timeout-=(long)100*1000*1000;
                Thread.sleep(100);
            }
            log.debug("获取锁失败:lockKey={}",this.lockKey);
            return Boolean.FALSE;
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }


    }

    public synchronized void unlock() {
        if (this.locked) {
            log.debug("正在尝试释放锁:lockKey={}",this.lockKey);
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.del(this.lockKey);
                this.setUnlock();
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
            }


            log.debug("释放锁成功:lockKey={}",this.lockKey);
        }
    }

    private Boolean setLock() {
        this.locked = true;
        return Boolean.TRUE;
    }

    private void setUnlock() {

        this.locked = false;
    }
}