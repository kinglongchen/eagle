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
        首先需要配置xml文件：
        ```Xml
               <!--配置一个AOP advisor bean-->
               <bean id="dlockInterceptor" class="com.iris.interceptor.DLockInterceptor">
                      <!--注入jedisPool-->
                      <property name="jedisPool" ref="jedisPool"/>
               </bean>

               <!--配置 aop-->
               <aop:config proxy-target-class="true">
                      <aop:pointcut id="dlockWrapper" expression="@annotation(com.iris.annotations.Dlock)"/>
                      <aop:advisor advice-ref="dlockInterceptor" pointcut-ref="dlockWrapper"/>
               </aop:config>

               <!--这个里面有一个方法需要使用分布式锁-->
               <bean id="dlockSpringTest" class="com.iris.dlock.spring.DlockSpringTest"/>

        ```
        DlockSpringTest代码如下:
        ```Java
        @Slf4j
        public class DlockSpringTest {
            /**
             * Dlock 表示这个这个方法是加了分布式锁的
             * @param p1
             * @param p2 有一个@LockKey注解表示这个参数将要作为分布式锁的key
             * @param p3
             */
            @Dlock
            public void dlock4Test(String p1,@LockKey ComplexKey p2,String p3) {
                log.info(Thread.currentThread().getName()+"===>");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info(Thread.currentThread().getName()+"<===");
            }
        }

        ```

        下面是调用DlockSpringTest.dlock4Test的示例:
        ```Java
        public class MainTest {
            public static void main(String[] args) {
                ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("test.dlock.xml");
                DlockSpringTest obj = ctx.getBean("dlockSpringTest",DlockSpringTest.class);
                ComplexKey complexKey = new ComplexKey();
                List<String> list = new ArrayList<String>();
                list.add("key1");
                list.add("key2ddddd");
                complexKey.setList(list);
                complexKey.setValue("key3");
                obj.dlock4Test("p1v1",complexKey,"p3v1");
            }
        }
        ```
