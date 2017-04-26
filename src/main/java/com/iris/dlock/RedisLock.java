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

    //�ͻ��˻�ȡ���ĳ�ʱʱ��10��=10*1000*1000*1000����
    private long timeoutTime = 10*1000*1000*1000l;

    //�ͻ��˻�ռ�������ʱ��Ϊ60��=60*1000*1000*1000����
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
            log.debug("���ڳ��Ի����:lockKey={}...",this.lockKey);
            while (timeout>0) {
                long currentTime = System.nanoTime();
                long expires = currentTime+expireTime;
                if (jedis.setnx(this.lockKey,String.valueOf(expires)) == 1) {
                    log.debug("�ɹ������:lockKey={}",this.lockKey);
                    return setLock();
                }
                log.debug("���ѱ�ռ��,���ڳ����Ƿ�ʱ:lockKey={}...", this.lockKey);
                String oldLockValue = jedis.get(this.lockKey);
//                log.debug("����ʱʱ��expires={},��ǰʱ��currentTime={}",oldLockValue,currentTime);
                if (oldLockValue != null && Long.parseLong(oldLockValue)<currentTime) {
                    log.debug("����ʱ,���ڳ��Ի�ȡ��ʱ����:lockKey={}...", this.lockKey);
                    String newLockValue = jedis.getSet(this.lockKey, String.valueOf(expires));
                    if (newLockValue != null && newLockValue.equals(oldLockValue)) {
                        log.debug("�ѻ�ȡ��ʱ����:lockKey={}",this.lockKey);
                        return setLock();
                    }
                }
                timeout-=(long)100*1000*1000;
                Thread.sleep(100);
            }
            log.debug("��ȡ��ʧ��:lockKey={}",this.lockKey);
            return Boolean.FALSE;
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }


    }

    public synchronized void unlock() {
        if (this.locked) {
            log.debug("���ڳ����ͷ���:lockKey={}",this.lockKey);
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


            log.debug("�ͷ����ɹ�:lockKey={}",this.lockKey);
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