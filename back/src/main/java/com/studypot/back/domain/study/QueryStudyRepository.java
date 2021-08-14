package com.studypot.back.domain.study;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryStudyRepository {

  private final JPAQueryFactory factory;

}
