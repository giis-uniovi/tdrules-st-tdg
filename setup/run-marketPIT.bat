@echo on
SET MARKET_DIR=%~dp0%\..\sut-market
cd %MARKET_DIR%
cmd /c mvn package -DskipTests=true

SET MARKETCORE_DIR=%~dp0%\..\sut-market\market-core
cd %MARKETDIR_DIR%
cmd /c mvn test-compile org.pitest:pitest-maven:mutationCoverage

pause