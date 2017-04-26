        一个分布式锁的实现
        有两种使用方式
        1.直接通过RedisLock来加锁,使用示例如下:
``` Java
    //    @Test
    public static void main(String[] args) {
        //获取一个JedisPool
        final JedisPool jedisPool = getJedisPool();
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            for (int i = 0; i < 10; i++) {
                executorService.execute(new Runnable() {
                    public void run() {
                        //获取一个redis分布式锁,其中第一个参数表示这个锁的KEY
                        RedisLock redisLock = new RedisLock("TEST-LOCK-KEY", jedisPool);
                        try {
                            //判断获取锁是否成功，如果获取失败，则抛出异常，可以根据自己的需要处理获取失败的锁
                            if (!redisLock.lock()) {
                                throw new RuntimeException("获取锁失败!");
                            }
                            //如果获取锁成功走如下逻辑
                            System.out.println(Thread.currentThread().getName() + "=====>");
                            Thread.sleep(1000);
                            System.out.println(Thread.currentThread().getName() + "<=====");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            //使用完锁后记得释放锁
                            redisLock.unlock();
                        }
                    }
                });
            }

        } finally {
            executorService.shutdown();
        }

    }
```
        2.通过切面实现使用锁：to be continue...