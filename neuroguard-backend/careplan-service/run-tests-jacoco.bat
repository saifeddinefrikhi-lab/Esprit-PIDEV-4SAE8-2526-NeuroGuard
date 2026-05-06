@echo off
REM ========================================
REM CarePlan Service - JaCoCo Test Runner
REM ========================================

echo.
echo ===== CarePlan Service - JaCoCo Test Suite =====
echo.

REM Check if Maven is installed
mvn --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven and add it to your system PATH
    pause
    exit /b 1
)

REM Get the directory of this script
cd /d "%~dp0"

echo Current directory: %cd%
echo.

REM Menu
echo Select an option:
echo 1. Run tests with JaCoCo report
echo 2. Run tests and open report
echo 3. Run specific test with coverage
echo 4. Clean and run full test suite
echo 5. Skip coverage checks (fast test)
echo 6. Exit
echo.

set /p CHOICE="Enter your choice (1-6): "

if "%CHOICE%"=="1" (
    echo.
    echo Running: mvn clean test
    call mvn clean test
    if errorlevel 1 (
        echo.
        echo Tests failed or coverage check failed
    ) else (
        echo.
        echo Report generated at: target/jacoco-reports/index.html
    )
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
    set /p TESTNAME="Enter test class name (e.g., CarePlanServiceTest): "
    echo Running: mvn clean test -Dtest=%TESTNAME%
    call mvn clean test -Dtest=%TESTNAME%
    echo.
    echo Report generated at: target/jacoco-reports/index.html
    pause
) else if "%CHOICE%"=="4" (
    echo.
    echo Running full clean test suite...
    call mvn clean test jacoco:report
    echo.
    echo All tests completed. Report: target/jacoco-reports/index.html
    pause
) else if "%CHOICE%"=="5" (
    echo.
    echo Running tests without coverage checks (skip JaCoCo validation)...
    call mvn clean test -Djacoco.skip=true
    pause
) else if "%CHOICE%"=="6" (
    echo Exiting...
    exit /b 0
) else (
    echo Invalid choice. Please select 1-6.
    pause
    goto :end
)

:end
echo.
pause
exit /b 0
