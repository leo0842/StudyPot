<a href="https://gitmoji.dev">
  <img src="https://img.shields.io/badge/gitmoji-%20😜%20😍-FFDD67.svg?style=flat-square" alt="Gitmoji">
</a>

# 토이 프로젝트 - StudyPot

스터디를 찾고있다면?

스터디가 화수분처럼 쏟아진다!

👉 [스터디팟](https://www.studypot.kr)

## :green_book: Table of Contents

- [Skills](#pushpin-skills)
- [Usage](#pencil2-usage)
- [API](#:stew:-구현된-기능)

## :pushpin: Skills

### Backend

- Java
- Spring Boot
- Spring JPA
- JUnit5, Mockito
- Gradle

### DevOps

- MySQL
- Docker
- Nginx
- AWS EC2
- S3
- Route 53

### Collaboration

- Git
- Wiki Confluence
- Jira
- Slack
- Swagger
- Figma

## :pencil2: Usage

resource 자원을 깃에 공유하지 않아 현재 환경변수 설정이 담기지 않은 상태입니다. 해당 서버를 실행하기 위해서는 다음을 따라주세요.

1. src/main에 resources 폴더를 만든 후 해당 파일 안에 application.yml 파일을 생성합니다.
2. application.yml 파일에 yaml 파일 형식에 맞게 jwt.secret, jwt.jwtExpirationTimeMs, jwt.refreshExpirationTimeMs 환경 변수를 설정합니다. <br>
   <br>
   예시)

```yml
jwt:
  secret: YOUR_SECRET_KEY
  jwtExpirationTimeMs: INTEGER
  refreshExpirationTimeMs: INTEGER
```

만료 시간의 단위는 밀리세컨드입니다. 환경 변수 설정이 완료되면 쉘에서 빌드와 실행을 할 수 있습니다.

```
~/StudyPot/back$ ./gradlew build
~/StudyPot/back$ ./gradlew bootRun
```

## :stew: 구현된 기능

### 0. 공통

- 스웨거
- 에러 전역적 관리
- JpaAudit
- Filter JWT 복호화
- Resolver 어노테이션 유저 정보 전달

### 1. 회원가입

- 특징
    - 암호화된 비밀번호 저장
- 추가 예정
    - SNS 로그인
    - email 인증

### 2. 회원 로그인

- 특징
    - 예외에 대한 Advice 구현
    - JWT AccessToken 및 JWT RefreshToken 발행

### 3. 필터

- 특징
    - 액세스 토큰을 복호화하여 Authentication 전달