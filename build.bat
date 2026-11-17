@echo off
cd /d "%~dp0"
set "FX_PATH=C:\Users\LENOVO\javafx-sdk-21\javafx-sdk-21\javafx-sdk-27.0.1"

if exist bin rmdir /S /Q bin
mkdir bin

if exist sources.txt del sources.txt
setlocal EnableDelayedExpansion
for /R src %%f in (*.java) do (
    set "source=%%f"
    echo "!source:\=/!">>sources.txt
)

javac -d bin --module-path "%FX_PATH%\lib" --add-modules javafx.controls,javafx.fxml @sources.txt

if %errorlevel% neq 0 (
    echo Build failed
    if not "%1"=="--nopause" pause
    exit /b %errorlevel%
)

xcopy src\algexplorer\ui\*.fxml bin\algexplorer\ui\ /Y /I
xcopy src\algexplorer\ui\*.css  bin\algexplorer\ui\ /Y /I

echo.
echo Build succeeded!
if not "%1"=="--nopause" pause
