# LockTest

낙관적 락, 비관적 락, 분산락을 다양한 시나리오에 대해 테스트 후 성능을 비교하기 위한 레포지토리

## 의존성

- Java 21
- Spring Boot 3

## 실행법

- .env 추가
  ```yml
  DB_USERNAME= # DB Username
  DB_DATABASE= # DB database name
  DB_PASSWORD= # DB Password
  REDIS_PORT= # Redis Port
  REDIS_PASSWORD= # Redis Password
  ```

- application.yml 추가
  ```yml
  # src/main/resources/application.yml
  server:
    port: # Server Port
    servlet:
      context-path: "/api"

  spring:
    profiles:
      active: "dev"
    datasource:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: # JDBC Connection URL
      username: ${DB_USERNAME}
      password: ${DB_PASSWORD}
    jpa:
      hibernate:
        ddl-auto: create
      show-sql: true
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
      password: ${REDIS_PASSWORD}
    output:
      ansi:
        enabled: ALWAYS
  
  logging:
    level:
      org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  
  custom:
    service:
      baseUrl: ${BASE_URL}

  ```

- 서버 실행
  ```shell
  ./gradlew bootrun
  ```
- 유닛 테스트
  ```shell
  ./gradlew clean test
  ```
- 부하 테스트
  ```shell
  docker-compose -f ./stress-test/docker-compose.stress.yml up -d
  ```
    - 결과 확인
        - localhost:13000 접속
        - id: admin, pw: admin, 비밀번호 변경 페이지 skip
        - 좌측 패널에서 Dashboard 클릭
    - 대시보드 출처
        - https://grafana.com/grafana/dashboards/18030-k6-prometheus-native-histograms/

## API

- 서버 실행 후 스웨거를 통해 확인 가능
- /api/swagger-ui/index.html