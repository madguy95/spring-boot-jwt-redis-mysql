package com.springjwt.security.redis;

import java.util.Optional;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;

@Slf4j
@Configuration
public class EmbeddedRedisConfig implements InitializingBean, DisposableBean {

	@Value("${redis.port}")
	private int redisPort;

	private RedisServer redisServer;

	@PreDestroy
	@Override
	public void destroy() throws Exception {
		Optional.ofNullable(redisServer).ifPresent(RedisServer::stop);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		redisServer = RedisServer.builder().port(redisPort).setting("maxheap 128M").build();
		redisServer.start();
	}
}