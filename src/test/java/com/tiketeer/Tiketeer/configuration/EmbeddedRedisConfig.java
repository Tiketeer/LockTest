package com.tiketeer.Tiketeer.configuration;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;

@Slf4j
@DisplayName("Embedded Redis 설정")
@TestConfiguration
public class EmbeddedRedisConfig {

	@Value("${spring.redis.host}")
	private String host;
	@Value("${spring.redis.port}")
	private int port;

	@Value("${spring.redis.maxmemory}")
	private int maxmemorySize;

	private RedisServer redisServer;

	@PostConstruct
	public void startRedis() throws IOException {
		this.redisServer = RedisServer.builder().port(port).setting("maxmemory " + maxmemorySize + "M").build();
		try {
			this.redisServer.start();
			log.info("레디스 서버 시작 성공");
		} catch (Exception e) {
			log.error("레디스 서버 시작 실패");
		}
	}

	@PreDestroy
	public void stopRedis() {
		this.redisServer.stop();
	}

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress("redis://" + host + ":" + port);
		return Redisson.create(config);
	}
}