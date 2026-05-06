#!/bin/bash
# CarePlan Service - JaCoCo Test Runner (Linux/Mac)

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo ""
echo "===== CarePlan Service - JaCoCo Test Suite ====="
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven and add it to your system PATH"
    exit 1
fi

echo "Current directory: $(pwd)"
echo ""

# Menu
echo "Select an option:"
echo "1. Run tests with JaCoCo report"
echo "2. Run tests and open report"
echo "3. Run specific test with coverage"
echo "4. Clean and run full test suite"
echo "5. Skip coverage checks (fast test)"
echo "6. Exit"
echo ""

read -p "Enter your choice (1-6): " CHOICE

case "$CHOICE" in
    1)
        echo ""
        echo "Running: mvn clean test"
        mvn clean test
        if [ $? -eq 0 ]; then
            echo ""
            echo "Report generated at: target/jacoco-reports/index.html"
        else
            echo ""
            echo "Tests failed or coverage check failed"
        fi
        ;;
    2)
        echo ""
        echo "Running: mvn clean test"
        mvn clean test
        if [ $? -eq 0 ]; then
            echo ""
            echo "Opening JaCoCo report..."
            if [[ "$OSTYPE" == "darwin"* ]]; then
                open target/jacoco-reports/index.html
            else
                xdg-open target/jacoco-reports/index.html
            fi
        fi
        ;;
    3)
        echo ""
        read -p "Enter test class name (e.g., CarePlanServiceTest): " TESTNAME
        echo "Running: mvn clean test -Dtest=$TESTNAME"
        mvn clean test -Dtest=$TESTNAME
        echo ""
        echo "Report generated at: target/jacoco-reports/index.html"
        ;;
    4)
        echo ""
        echo "Running full clean test suite..."
        mvn clean test jacoco:report
        echo ""
        echo "All tests completed. Report: target/jacoco-reports/index.html"
        ;;
    5)
        echo ""
        echo "Running tests without coverage checks (skip JaCoCo validation)..."
        mvn clean test -Djacoco.skip=true
        ;;
    6)
        echo "Exiting..."
        exit 0
        ;;
    *)
        echo "Invalid choice. Please select 1-6."
        exit 1
        ;;
esac

echo ""
