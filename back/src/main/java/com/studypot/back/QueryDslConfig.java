package com.studypot.back;

import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QueryDslConfig {

  private final EntityManager entityManager;
  
  @Bean
  public JPAQueryFactory jpaQueryFactory() {

    return new JPAQueryFactory(entityManager);
  }
}
