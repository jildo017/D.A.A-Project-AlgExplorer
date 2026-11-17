@echo off
cd /d "%~dp0"
if not exist out mkdir out
if exist sources.txt del sources.txt
for /R src %%f in (*.java) do echo %%f>>sources.txt
javac -encoding UTF-8 -d out @sources.txt
if %errorlevel% neq 0 (
    echo Build failed!
    if not "%1"=="--nopause" pause
    exit /b %errorlevel%
)
echo Launching Algorithm Explorer...
java -cp out ui.MainFrame
if not "%1"=="--nopause" pause
