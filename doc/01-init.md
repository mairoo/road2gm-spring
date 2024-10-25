# 프로젝트 생성

https://start.spring.io/

Project:

- Build Tool: Gradle - Groovy
- Language: Java
- Spring Boot: 3.3.5
- Packaging: Jar
- Java Version: 17

Project Metadata:

- Group: kr.co.road2gm
- Artifact: api
- Name: api
- Description: Road2GM backend API server
- Package name: kr.co.road2gm.api

Dependencies:

- Spring Web
- Spring Data JPA
- Spring Security
- Lombok
- Validation
- MariaDB Driver
- Spring Boot DevTools
- Spring Configuration Processor

# MariaDB 연결 설정

```
spring:
  application.name: api
  datasource:
    driver-class-name: org.mariadb.jdbc.Driver
    url: "jdbc:mariadb://127.0.0.1/road2gm"
    username: road2gm
    password: encrypted_password
    hikari:
      connectionInitSql: "SET NAMES utf8mb4"
```

# 스프링 데이터 JPA 설정
```
  jpa:
    hibernate:
      ddl-auto: validate # validate, create, create-drop., update, none
    # `hibernate.ddl-auto` 설정이 `generate-ddl`보다 우선하므로 설정이 무시된다.
    # generate-ddl: true
    properties:
      hibernate:
        format_sql: true
        # show_sql: true # stdout
        highlight_sql: true
        use_sql_comments: true # show JPQL (not SQL)
        default_batch_fetch_size: 500
    open-in-view: false # lazy loading queries may be performed during view rendering if true
```
