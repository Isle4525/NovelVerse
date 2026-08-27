@echo off
setlocal
cd /d "%~dp0"

echo Starting NovelVerse Desktop...

if exist mvnw.cmd (
    call mvnw.cmd -q -DskipTests javafx:run
    if %errorlevel%==0 goto :eof
)

where mvn >nul 2>nul
if %errorlevel%==0 (
    call mvn -q -DskipTests javafx:run
    goto :eof
)

echo Maven was not found.
echo Install Maven or fix mvnw.cmd to launch the desktop app.
pause
