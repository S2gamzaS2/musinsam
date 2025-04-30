package com.musinsam.eventservice.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.musinsam.eventservice.application.dto.response.ResEventGetByEventIdDtoApiV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, ResEventGetByEventIdDtoApiV1> eventTemplate(
      RedisConnectionFactory redisConnectionFactory
  ) {

    RedisTemplate<String, ResEventGetByEventIdDtoApiV1> template = new RedisTemplate<>();

    // ObjectMapper 설정 추가
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.activateDefaultTyping(
        objectMapper.getPolymorphicTypeValidator(),
        DefaultTyping.NON_FINAL,
        As.PROPERTY);

    RedisSerializer<Object> serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(RedisSerializer.string());
    template.setValueSerializer(serializer);
    template.setHashKeySerializer(RedisSerializer.string());
    template.setHashValueSerializer(serializer);

    return template;
  }

  @Bean
  public ValueOperations<String, ResEventGetByEventIdDtoApiV1> valueOps(
      RedisTemplate<String, ResEventGetByEventIdDtoApiV1> redisTemplate) {
    return redisTemplate.opsForValue();
  }

}
