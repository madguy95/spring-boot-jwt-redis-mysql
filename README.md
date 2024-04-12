# spring-boot-jwt-redis-mysql
spring-boot-jwt-redis-mysql websocket with sockjs

Based on:

* [springboot3](https://spring.io/projects/spring-boot)
* [jwt](https://bezkoder.com/angular-spring-boot-jwt-auth/)
* [redis](https://redis.io/)
* [mysql](https://dev.mysql.com/doc/)
* [PostgreSQL](https://www.postgresql.org/docs/)
* [Sockjs](https://spring.io/guides/gs/messaging-stomp-websocket/)

## Configure Spring Datasource, JPA, App properties
Open `src/main/resources/application.properties`
- For MySQL 8
```
spring.datasource.url= jdbc:mysql://localhost:3306/testdb?useSSL=false
spring.datasource.username= root
spring.datasource.password= 123456
```
- For Redis
```
spring.data.redis.database=0
spring.data.redis.host=localhost
spring.data.redis.port=16379
spring.data.redis.password=mypass
```
## Run Spring Boot application
```
mvn spring-boot:run
```

## Run following SQL insert statements
```
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```
