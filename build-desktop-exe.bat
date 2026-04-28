@echo off
setlocal
cd /d "%~dp0"

set APP_NAME=NovelVerseDesktop
set MAIN_JAR=target\novelverse-0.0.1-SNAPSHOT.jar
set EXEC_JAR=target\novelverse-0.0.1-SNAPSHOT-exec.jar
set OUTPUT_DIR=dist
set INPUT_DIR=%OUTPUT_DIR%\input

echo Building desktop exe package...

if exist mvnw.cmd (
    call mvnw.cmd -DskipTests package
) else (
    call mvn -DskipTests package
)

if not exist "%MAIN_JAR%" (
    echo Jar file was not created: %MAIN_JAR%
    pause
    exit /b 1
)

if exist "%INPUT_DIR%" rmdir /s /q "%INPUT_DIR%"
mkdir "%INPUT_DIR%"

copy "%MAIN_JAR%" "%INPUT_DIR%\" >nul
if exist "%EXEC_JAR%" copy "%EXEC_JAR%" "%INPUT_DIR%\" >nul
if exist "target\libs" copy "target\libs\*.jar" "%INPUT_DIR%\" >nul

where jpackage >nul 2>nul
if errorlevel 1 (
    echo jpackage was not found in PATH.
    echo Use JDK 17+ with jpackage available.
    pause
    exit /b 1
)

jpackage ^
  --type exe ^
  --name %APP_NAME% ^
  --input %INPUT_DIR% ^
  --main-jar novelverse-0.0.1-SNAPSHOT.jar ^
  --main-class com.novelverse.novelverse.desktop.NovelVerseDesktopLauncher ^
  --dest %OUTPUT_DIR%

echo Done. EXE package created in %OUTPUT_DIR%
pause
