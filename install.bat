@echo off
setlocal enabledelayedexpansion

echo.
echo ============================================
echo   QuickStart Installer (Windows)
echo ============================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed or not in PATH.
    echo Please install Java 17 or higher and try again.
    echo.
    pause
    exit /b 1
)

REM Check if quickstart.jar exists
if not exist "quickstart.jar" (
    echo [ERROR] quickstart.jar not found in current directory.
    echo Please run 'mvn clean package' first.
    echo.
    pause
    exit /b 1
)

echo [1/4] Creating QuickStart directory...
mkdir "%USERPROFILE%\.quickstart" 2>nul

echo [2/4] Copying JAR file...
copy /Y quickstart.jar "%USERPROFILE%\.quickstart\" >nul

echo [3/4] Creating command wrapper...
REM Create qs.bat (short alias)
(
echo @echo off
echo java -jar "%%USERPROFILE%%\.quickstart\quickstart.jar" %%*
) > "%USERPROFILE%\.quickstart\qs.bat"

echo [4/4] Adding to PATH...

REM Get current user PATH
for /f "skip=2 tokens=3*" %%a in ('reg query HKCU\Environment /v PATH 2^>nul') do set "CURRENT_PATH=%%a %%b"

REM Check if already in PATH
echo !CURRENT_PATH! | findstr /C:"%USERPROFILE%\.quickstart" >nul
if errorlevel 1 (
    REM Not in PATH, add it
    setx PATH "%CURRENT_PATH%;%USERPROFILE%\.quickstart" >nul
    echo    ^> Added to PATH successfully
) else (
    echo    ^> Already in PATH, skipping
)

echo.
echo ============================================
echo   Installation Complete!
echo ============================================
echo.
echo QuickStart has been installed to:
echo   %USERPROFILE%\.quickstart
echo.
echo IMPORTANT: Close this terminal and open a NEW one
echo to use the commands.
echo.
echo Try these commands in your new terminal:
echo   qs init
echo   qs --help
echo.
pause