@echo off
cd /d "%~dp0"
set "FX_PATH=C:\Users\LENOVO\javafx-sdk-21\javafx-sdk-21\javafx-sdk-27.0.1"

if not exist bin (
    echo "bin directory not found. Please run build.bat first."
    pause
    exit /b 1
)

echo Starting Algorithm Explorer...
java --module-path "bin;%FX_PATH%\lib" --add-modules javafx.controls,javafx.fxml -m algexplorer/algexplorer.Main

echo.
echo Application finished.
pause
