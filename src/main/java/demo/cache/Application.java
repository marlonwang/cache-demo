package demo.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

/**
 * Hello cache!
 * cache-demo
 * @date 2015-9-6
 * @author wangwei
 */
@EnableAutoConfiguration
@ImportResource("classpath:spring.xml")
public class Application 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
	
    public static void main( String[] args )
    {
        LOGGER.info("Begin to run yijia cache-demo !");
        SpringApplication.run(Application.class, args);
    }
}
