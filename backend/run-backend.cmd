@echo off
set DB_PASSWORD=Root@12345
set DB_USER=root
mvnw.cmd spring-boot:run > be-live3.log 2>&1
