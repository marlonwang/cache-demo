package demo.cache.redis;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.springframework.util.StringUtils;

import demo.cache.model.Admin;
import demo.cache.util.JsonUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisClientTest {

	private JedisClient jedis = new JedisClient("127.0.0.1", 16379);
	
	@Test
	public void test()
	{
		jedis.put("user1", "user1_info");
	}

	@Test
	public void testAtom()
	{
		for (int i = 0; i < 10; i++)
		{
			jedis.atomIncrease("testkey");
		}

		System.out.println(jedis.getAtomCount("testkey"));

		for(int i = 0 ; i < 5;i++)
		{
		  jedis.atomDecrease("testkey");
		}
		 
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

		@SuppressWarnings("resource")
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
		System.out.println("线程初始化完毕!");
		while(true)
		{
			Thread.sleep(10000);
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
		for (String key : keys)
		{
			System.out.println(key +":"+ jedis.get(key, String.class));
		}
	}
	
	@Test
	public void testJedisPipe()
	{
		String[] keys = jedis.getCacheKeys("user*");
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
			dataMap.put("test_"+String.valueOf(i), "test" + i);
		}
		long begin = System.currentTimeMillis();
		jedis.batchPut(dataMap);
		long end1 = System.currentTimeMillis();
		String[] keys = jedis.getCacheKeys("test_*");
		jedis.deleteKeys(keys);
		long end2 = System.currentTimeMillis();
		System.out.println("add 10000 keys, cost:"+(end1-begin)+"\n"
				+ "delete 10000 keys, cost:"+(end2-end1));
	}
	
	@Test
	public void testAddObject()
	{
		
		//System.out.println(System.currentTimeMillis());
		Admin admin = new Admin();
		admin.setBlogUrl("http://blog.163.com/marlonwang/");
		admin.setImageUrl("http://blog.163.com/marlonwang/icon/xxx.png");
		admin.setMail("marlonwang@163.com");
		admin.setName("marlonwang");
		admin.setPasswd("123456");
		admin.setPrivilege(1);
		jedis.put("admin_1", admin);
		jedis.put("admin_2", JsonUtils.obj2json(admin));
		
		System.out.println(jedis.get("admin_1", Admin.class));//demo.cache.model.Admin@51a8344f
		System.out.println(jedis.get("admin_2", String.class));
	}
	
	@Test
	public void testGetRedis()
	{
		System.out.println(jedis.getRedisServerIp() +" "+jedis.getRedisServerPort());
		System.out.println(jedis.getJedis());
		System.out.println(JsonUtils.obj2json(jedis.getJedis())); //null
	}
	
	@Test
	public void testGetActive(){
		System.out.println(jedis.getJedisPool());
		System.out.println(jedis.getActive());
	}
	
	@Test
	public void putKeyWithExpire()
	{
		jedis.put("expireKey",System.currentTimeMillis(), 50);
		int count = 0;
		while(!StringUtils.isEmpty(jedis.get("expireKey", String.class)))
		{
			System.out.println(jedis.get("expireKey", String.class));
			try {
				Thread.sleep(1000);
				count++;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(count+" at "+System.currentTimeMillis());
	}
}
