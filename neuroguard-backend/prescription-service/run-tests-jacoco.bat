@echo off
REM ========================================
REM Prescription Service - JaCoCo Test Runner
REM ========================================

echo.
echo ===== Prescription Service - JaCoCo Test Suite =====
echo.

REM Check if Maven is installed
mvn --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven is not installed or not in PATH
    pause
    exit /b 1
)

cd /d "%~dp0"
echo Current directory: %cd%
echo.

echo Select an option:
echo 1. Run tests with JaCoCo report
echo 2. Run tests and open report
echo 3. Run specific test with coverage
echo 4. Skip coverage checks (fast test)
echo 5. Exit
echo.

set /p CHOICE="Enter your choice (1-5): "

if "%CHOICE%"=="1" (
    echo.
    echo Running: mvn clean test
    call mvn clean test
    echo.
    echo Report: target\jacoco-reports\index.html
    pause
) else if "%CHOICE%"=="2" (
    echo.
    echo Running: mvn clean test
    call mvn clean test
    if errorlevel 0 (
        echo.
        echo Opening JaCoCo report...
        start target\jacoco-reports\index.html
    )
    pause
) else if "%CHOICE%"=="3" (
    echo.
    set /p TESTNAME="Enter test class name: "
    echo Running: mvn clean test -Dtest=%TESTNAME%
    call mvn clean test -Dtest=%TESTNAME%
    pause
) else if "%CHOICE%"=="4" (
    echo.
    echo Running tests without coverage checks...
    call mvn clean test -Djacoco.skip=true
    pause
) else if "%CHOICE%"=="5" (
    exit /b 0
) else (
    echo Invalid choice
    pause
)
