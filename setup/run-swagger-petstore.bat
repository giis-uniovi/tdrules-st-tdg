@echo on
SET PETSTORE_DIR=%~dp0%\..\swagger-petstore-main-fork
cd %PETSTORE_DIR%
cmd /c mvn package -DskipTests=true
docker build -t swagger-petstore .
docker stop swagger-petstore
docker rm swagger-petstore
docker run -d -p 8081:8080 --name swagger-petstore swagger-petstore
pause