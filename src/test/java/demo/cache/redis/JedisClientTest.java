package demo.cache.redis;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisClientTest {

	private JedisClient jedis = new JedisClient("127.0.0.1", 16379);
	
	@Test
	public void test()
	{
		jedis.put("user1", "user1_user");

	}

	@Test
	public void testAtom()
	{
		for (int i = 0; i < 10; i++)
		{
			jedis.atomIncrease("testkey");
		}

		System.out.println(jedis.getAtomCount("testkey"));

		jedis.put("testkey", 0);

		/*
		 * for(int i = 0 ; i < 40;i++)
		 * {
		 * jedis.atomDecrease("testkey");
		 * }
		 */
		System.out.println(jedis.getAtomCount("testkey"));
	}

	@Test
	public void testGetKey() throws InterruptedException
	{
		JedisPoolConfig config = new JedisPoolConfig();
		// maxIdle: 链接池中最大空闲的连接数,默认为8.
		config.setMaxIdle(5);
		
		// minIdle: 连接池中最少空闲的连接数,默认为0.
		config.setMinIdle(1);
		
		// 最大连接数
		config.setMaxTotal(50);
		
		// 当连接池资源耗尽时，调用者最大阻塞的时间，超时将跑出异常。单位，毫秒数;默认为-1.表示永不超时.
		config.setMaxWaitMillis(1000 * 10);
		
		// minEvictableIdleTimeMillis: 连接空闲的最小时间，达到此值后空闲连接将可能会被移除。负值(-1)表示不移除，默认为半个小时，这里设置为5分钟
		config.setMinEvictableIdleTimeMillis(300000);

		// testOnBorrow: 向调用者输出链接资源时，是否检测是有有效，如果无效则从连接池中移除，并尝试继续获取。默认为false。
		config.setTestOnBorrow(true);

		// testOnReturn: 向连接池归还链接时，是否检测链接对象的有效性。默认为false。
		config.setTestOnReturn(true);

		JedisPool jedisPool = new JedisPool(config, "127.0.0.1", 16379);
		ExecutorService exec = Executors.newFixedThreadPool(1000);
		for (int i = 0; i < 1000; i++)
		{
			Jedis jedis = null;
			try
			{
				jedis = jedisPool.getResource();
			} catch(Exception e)
			{
				i--;
				e.printStackTrace();
			}
			if (null == jedis)
			{
				continue;
			}
			
			Thread t = new Thread(new TestThread(jedis));
			exec.execute(t);
		}
//		Thread t0 = new Thread(new TestThread(jedis));
//		Thread t1 = new Thread(new TestThread(jedis));
//		Thread t2 = new Thread(new TestThread(jedis));
//		Thread t3 = new Thread(new TestThread(jedis));
//		Thread t4 = new Thread(new TestThread(jedis));
//		Thread t5 = new Thread(new TestThread(jedis));
//		Thread t6 = new Thread(new TestThread(jedis));
//		Thread t7 = new Thread(new TestThread(jedis));
//		
//		t0.start();
//		t1.start();
//		t2.start();
//		t3.start();
		
//		ExecutorService exec = Executors.newFixedThreadPool(5);
//		exec.execute(t0);
//		exec.execute(t1);
//		exec.execute(t2);
//		exec.execute(t3);
//		exec.execute(t4);
//		exec.execute(t5);
//		exec.execute(t6);
//		exec.execute(t7);
		System.out.println("线程初始化完毕!");
		while(true)
		{
			Thread.sleep(1000000);
		}
	}
	
	class TestThread implements Runnable
	{
		private Jedis jedisClient ;
		
		public TestThread(Jedis jedisClient)
		{
			this.jedisClient = jedisClient;
		}
		@Override
		public void run()
		{
			while(true)
			{
				long currentTime = System.currentTimeMillis();
				jedisClient.incr("TEST_INCR");
				System.out.println("cost time:" + (System.currentTimeMillis() - currentTime));
//				System.out.println("Thread");
				try
				{
					Thread.sleep(500);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	@Test
	public void testGetKeys()
	{
		String[] keys = jedis.getCacheKeys("*");
		//jedis.deleteKeys(keys);
		for (String key : keys)
		{
			System.out.println(key +":"+ jedis.get(key, String.class));
		}
		
	}
	
	@Test
	public void testJedisPipe()
	{
		String[] keys = jedis.getCacheKeys("SFZDY_USER_*");
		long beginTime = System.currentTimeMillis();
		for (String key : keys)
		{
			System.out.println(key);
		}
		System.out.println(keys.length);
		jedis.deleteKeys(keys);
		System.out.println("Batch Cost time:" + (System.currentTimeMillis() - beginTime));
		
		
	}
	
	@Test
	public void testAddKeys()
	{
		HashMap<String, Object> dataMap = new HashMap<String,Object>(); 
		for (int i = 0 ; i < 10000; i++)
		{
			dataMap.put(String.valueOf(i), "fjdsaljewrjklfdsafdsafdsa" + i);
		}
		jedis.batchPut(dataMap);
	}
	
	@Test
	public void testBytes()
	{
		String userInfo = "{\"id\":581,\"phoneNumber\":\"13751113766\",\"email\":null,\"userToken\":\"B7DC8EBCD6E52D0967E2862DF769562E\",\"password\":\"123456\",\"headImageUrl\":null,\"registTime\":\"2014/11/04 02:28:27\",\"productCode\":\"GAME_BOOSTER\",\"vipExpireTime\":null,\"vipLevel\":null,\"score\":0,\"isVip\":false,\"isSignIn\":false}";
		System.out.println(userInfo.getBytes().length);
	}

}