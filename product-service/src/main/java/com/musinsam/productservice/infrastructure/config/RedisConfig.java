package com.musinsam.productservice.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.musinsam.productservice.application.dto.response.ResProductGetByProductIdDtoApiV1;
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
  public RedisTemplate<String, ResProductGetByProductIdDtoApiV1> productTemplate(
      RedisConnectionFactory redisConnectionFactory) {

    RedisTemplate<String, ResProductGetByProductIdDtoApiV1> template = new RedisTemplate<>();

    // ObjectMapper 설정 추가
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.activateDefaultTyping(
        objectMapper.getPolymorphicTypeValidator(),
        DefaultTyping.NON_FINAL,
        As.PROPERTY);

    // serializer 생성, 적용
    RedisSerializer<Object> serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(RedisSerializer.string());
    template.setValueSerializer(serializer);
    template.setHashKeySerializer(RedisSerializer.string());
    template.setHashValueSerializer(serializer);

    return template;
  }

  @Bean
  public ValueOperations<String, ResProductGetByProductIdDtoApiV1> valueOps(
      RedisTemplate<String, ResProductGetByProductIdDtoApiV1> redisTemplate) {
    return redisTemplate.opsForValue();
  }

}
