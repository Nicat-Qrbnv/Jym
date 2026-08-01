package com.epam.jym.trainerworkload.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@Testcontainers
public abstract class AbstractIntegrationTest {

  @Container static final MongoDBContainer mongo = new MongoDBContainer("mongo:8").withReplicaSet();

  @Container
  static final GenericContainer<?> redis = new GenericContainer<>("redis:7").withExposedPorts(6379);

  @DynamicPropertySource
  static void overrideInfrastructureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.mongodb.uri", mongo::getReplicaSetUrl);
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
  }
}
