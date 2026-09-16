@echo off
@REM Maven Wrapper startup script

setlocal enabledelayedexpansion

@REM Get the directory where this script is located
set "SCRIPT_DIR=%~dp0"

@REM The maven-wrapper.jar is in the same directory as this script
set "WRAPPER_JAR=%SCRIPT_DIR%maven-wrapper.jar"
set "WRAPPER_PROPERTIES=%SCRIPT_DIR%maven-wrapper.properties"

if not exist "%WRAPPER_JAR%" (
    echo Error: Maven wrapper jar not found: %WRAPPER_JAR%
    exit /b 1
)

if not exist "%WRAPPER_PROPERTIES%" (
    echo Warning: Maven wrapper properties not found: %WRAPPER_PROPERTIES%
)

@REM Get Java from JAVA_HOME or default to system java
set "JAVA_CMD=java"
if not "%JAVA_HOME%" == "" (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
    )
)

@REM Check if java is available
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
    ) else (
        echo Error: Java not found. Please set JAVA_HOME.
        exit /b 1
    )
)

@REM Execute Maven Wrapper
"%JAVA_CMD%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*

endlocal
