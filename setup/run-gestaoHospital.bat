@echo on
SET GESTAOHOSPITAL_DIR=%~dp0%\..\sut-gestaoHospital
cd %GESTAOHOSPITAL_DIR%
cmd /c mvn package -DskipTests=true
docker-compose down
docker-compose up -d --force-recreate --build
pause