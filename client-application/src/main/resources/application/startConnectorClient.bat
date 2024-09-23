@echo off
rem This is the automatically built startup script for the domibusConnectorClient.
rem To be able to run the JAVA_HOME system environment variable must be set properly.

if exist "%JAVA_HOME%" goto okJava
call setenv.bat
if exist "%JAVA_HOME%" goto okJava
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:okJava


set "CURRENT_DIR=%cd%"

set "CONNECTOR_CLIENT_BASE_PATH=%CURRENT_DIR%"

set "CLASSPATH=%CURRENT_DIR%\bin\*"
echo %CLASSPATH%

title "DomibusConnectorClient"


@echo on
"%JAVA_HOME%\bin\java" -Dloader.path=%CURRENT_DIR%\lib -cp "%CLASSPATH%" -Dconnector-client.properties="config\connector-client.properties" "org.springframework.boot.loader.launch.PropertiesLauncher"
pause
:end
