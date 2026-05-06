#!/bin/bash
# Prescription Service - JaCoCo Test Runner

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo ""
echo "===== Prescription Service - JaCoCo Test Suite ====="
echo ""

if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed"
    exit 1
fi

echo "Current directory: $(pwd)"
echo ""
echo "Select an option:"
echo "1. Run tests with JaCoCo report"
echo "2. Run tests and open report"
echo "3. Run specific test with coverage"
echo "4. Skip coverage checks (fast test)"
echo "5. Exit"
echo ""

read -p "Enter your choice (1-5): " CHOICE

case "$CHOICE" in
    1)
        echo ""
        echo "Running: mvn clean test"
        mvn clean test
        echo ""
        echo "Report: target/jacoco-reports/index.html"
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
        read -p "Enter test class name: " TESTNAME
        echo "Running: mvn clean test -Dtest=$TESTNAME"
        mvn clean test -Dtest=$TESTNAME
        ;;
    4)
        echo ""
        echo "Running tests without coverage checks..."
        mvn clean test -Djacoco.skip=true
        ;;
    5)
        exit 0
        ;;
    *)
        echo "Invalid choice"
        exit 1
        ;;
esac
