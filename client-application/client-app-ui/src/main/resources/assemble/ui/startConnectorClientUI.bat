@echo off
rem This is the automatically built startup script for the DomibusStandaloneConnector.
rem To be able to run the JAVA_HOME system environment variable must be set properly.

if exist "%JAVA_HOME%" goto okJava
call setenv.bat
if exist "%JAVA_HOME%" goto okJava
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:okJava


set "CURRENT_DIR=%cd%"

set "CLASSPATH=%CURRENT_DIR%\bin\*"
echo %CLASSPATH%


title "DomibusConnectorClient-UserInterface"


@echo on
"%JAVA_HOME%\bin\java" -cp "%CLASSPATH%" -Dspring.config.location="config\connector-client-UI.properties" "org.springframework.boot.loader.PropertiesLauncher" 
pause
:end
