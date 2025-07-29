#!/bin/bash

# Tactical Command Hub - Test Script
# This script runs comprehensive tests with detailed reporting

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="tactical-command-hub"
REPORTS_DIR="target/test-reports"
COVERAGE_DIR="target/site/jacoco"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Tactical Command Hub - Test Suite    ${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Function to print step headers
print_step() {
    echo -e "${YELLOW}>>> $1${NC}"
}

# Function to print success messages
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Function to print error messages
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Function to print info messages
print_info() {
    echo -e "${CYAN}ℹ $1${NC}"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -t, --type          Test type (unit|integration|all)"
    echo "  -c, --coverage      Generate coverage report"
    echo "  -r, --reports       Generate detailed HTML reports"
    echo "  -f, --fast          Skip time-consuming tests"
    echo "  -v, --verbose       Verbose output"
    echo "  -h, --help          Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 -t unit -c"
    echo "  $0 -t integration -r"
    echo "  $0 -t all -c -r -v"
    echo ""
}

# Parse command line arguments
TEST_TYPE="all"
COVERAGE=false
REPORTS=false
FAST=false
VERBOSE=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -t|--type)
            TEST_TYPE="$2"
            shift 2
            ;;
        -c|--coverage)
            COVERAGE=true
            shift
            ;;
        -r|--reports)
            REPORTS=true
            shift
            ;;
        -f|--fast)
            FAST=true
            shift
            ;;
        -v|--verbose)
            VERBOSE=true
            shift
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Validate test type
if [[ ! "$TEST_TYPE" =~ ^(unit|integration|all)$ ]]; then
    print_error "Invalid test type: $TEST_TYPE"
    echo "Valid types: unit, integration, all"
    exit 1
fi

echo -e "🧪 ${BLUE}Test Type:${NC} $TEST_TYPE"
echo -e "📊 ${BLUE}Coverage:${NC} $([ "$COVERAGE" = true ] && echo "enabled" || echo "disabled")"
echo -e "📋 ${BLUE}Reports:${NC} $([ "$REPORTS" = true ] && echo "enabled" || echo "disabled")"
echo -e "⚡ ${BLUE}Fast Mode:${NC} $([ "$FAST" = true ] && echo "enabled" || echo "disabled")"
echo ""

# Check prerequisites
print_step "Checking prerequisites..."

# Check Java
if ! command -v java &> /dev/null; then
    print_error "Java not found. Please install Java 17 or higher."
    exit 1
fi
print_success "Java available"

# Check Maven
if ! command -v mvn &> /dev/null; then
    print_error "Maven not found. Please install Maven 3.8 or higher."
    exit 1
fi
print_success "Maven available"

# Check Docker (for integration tests)
if [ "$TEST_TYPE" = "integration" ] || [ "$TEST_TYPE" = "all" ]; then
    if ! command -v docker &> /dev/null; then
        print_error "Docker required for integration tests but not found."
        exit 1
    fi
    print_success "Docker available"
fi

# Prepare test environment
print_step "Preparing test environment..."

# Clean previous test results
rm -rf "$REPORTS_DIR" "$COVERAGE_DIR"
mkdir -p "$REPORTS_DIR"

# Ensure test database is clean
if [ "$TEST_TYPE" = "integration" ] || [ "$TEST_TYPE" = "all" ]; then
    print_info "Preparing test containers..."
    # TestContainers will handle database setup automatically
fi

print_success "Test environment prepared"

# Maven options
MAVEN_OPTS="-Xmx1024m"
TEST_OPTS=""

if [ "$VERBOSE" = true ]; then
    TEST_OPTS="$TEST_OPTS -X"
else
    TEST_OPTS="$TEST_OPTS -q"
fi

if [ "$FAST" = true ]; then
    TEST_OPTS="$TEST_OPTS -Dtest.timeout=5000"
fi

# Initialize test statistics
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0
SKIPPED_TESTS=0
START_TIME=$(date +%s)

# Function to extract test statistics from Maven output
extract_test_stats() {
    local output="$1"
    local tests=$(echo "$output" | grep -o "Tests run: [0-9]*" | grep -o "[0-9]*" | head -1)
    local failures=$(echo "$output" | grep -o "Failures: [0-9]*" | grep -o "[0-9]*" | head -1)
    local errors=$(echo "$output" | grep -o "Errors: [0-9]*" | grep -o "[0-9]*" | head -1)
    local skipped=$(echo "$output" | grep -o "Skipped: [0-9]*" | grep -o "[0-9]*" | head -1)
    
    TOTAL_TESTS=${tests:-0}
    FAILED_TESTS=$((${failures:-0} + ${errors:-0}))
    SKIPPED_TESTS=${skipped:-0}
    PASSED_TESTS=$((TOTAL_TESTS - FAILED_TESTS - SKIPPED_TESTS))
}

# Run unit tests
if [ "$TEST_TYPE" = "unit" ] || [ "$TEST_TYPE" = "all" ]; then
    print_step "Running unit tests..."
    
    UNIT_OUTPUT=$(mvn test $TEST_OPTS 2>&1)
    UNIT_EXIT_CODE=$?
    
    if [ "$VERBOSE" = true ]; then
        echo "$UNIT_OUTPUT"
    fi
    
    extract_test_stats "$UNIT_OUTPUT"
    
    if [ $UNIT_EXIT_CODE -eq 0 ]; then
        print_success "Unit tests completed: $PASSED_TESTS passed, $FAILED_TESTS failed, $SKIPPED_TESTS skipped"
    else
        print_error "Unit tests failed: $FAILED_TESTS failures"
        if [ "$VERBOSE" = false ]; then
            echo "Run with -v for detailed output"
        fi
        exit 1
    fi
fi

# Run integration tests
if [ "$TEST_TYPE" = "integration" ] || [ "$TEST_TYPE" = "all" ]; then
    print_step "Running integration tests..."
    
    INTEGRATION_OUTPUT=$(mvn verify -P integration-tests $TEST_OPTS 2>&1)
    INTEGRATION_EXIT_CODE=$?
    
    if [ "$VERBOSE" = true ]; then
        echo "$INTEGRATION_OUTPUT"
    fi
    
    extract_test_stats "$INTEGRATION_OUTPUT"
    
    if [ $INTEGRATION_EXIT_CODE -eq 0 ]; then
        print_success "Integration tests completed: $PASSED_TESTS passed, $FAILED_TESTS failed, $SKIPPED_TESTS skipped"
    else
        print_error "Integration tests failed: $FAILED_TESTS failures"
        if [ "$VERBOSE" = false ]; then
            echo "Run with -v for detailed output"
        fi
        exit 1
    fi
fi

# Generate coverage report
if [ "$COVERAGE" = true ]; then
    print_step "Generating coverage report..."
    
    mvn jacoco:report $TEST_OPTS > /dev/null 2>&1
    
    if [ -f "$COVERAGE_DIR/index.html" ]; then
        # Extract coverage percentage
        if command -v grep &> /dev/null && [ -f "$COVERAGE_DIR/index.html" ]; then
            COVERAGE_PERCENT=$(grep -o "Total[^%]*%" "$COVERAGE_DIR/index.html" | grep -o "[0-9]*%" | head -1)
            print_success "Coverage report generated: $COVERAGE_PERCENT"
        else
            print_success "Coverage report generated"
        fi
        
        print_info "Coverage report: $COVERAGE_DIR/index.html"
    else
        print_error "Failed to generate coverage report"
    fi
fi

# Generate detailed HTML reports
if [ "$REPORTS" = true ]; then
    print_step "Generating detailed test reports..."
    
    # Surefire reports (unit tests)
    if [ -d "target/surefire-reports" ]; then
        cp -r target/surefire-reports/* "$REPORTS_DIR/" 2>/dev/null || true
    fi
    
    # Failsafe reports (integration tests)
    if [ -d "target/failsafe-reports" ]; then
        cp -r target/failsafe-reports/* "$REPORTS_DIR/" 2>/dev/null || true
    fi
    
    # Generate HTML test report summary
    cat > "$REPORTS_DIR/test-summary.html" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>Tactical Command Hub - Test Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .header { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
        .summary { background-color: #ecf0f1; padding: 20px; border-radius: 5px; margin: 20px 0; }
        .success { color: #27ae60; font-weight: bold; }
        .error { color: #e74c3c; font-weight: bold; }
        .info { color: #3498db; }
        table { border-collapse: collapse; width: 100%; margin: 20px 0; }
        th, td { border: 1px solid #bdc3c7; padding: 12px; text-align: left; }
        th { background-color: #34495e; color: white; }
        .passed { background-color: #d5f4e6; }
        .failed { background-color: #fadbd8; }
        .skipped { background-color: #fef9e7; }
    </style>
</head>
<body>
    <h1 class="header">Tactical Command Hub - Test Report</h1>
    
    <div class="summary">
        <h2>Test Summary</h2>
        <p><strong>Generated:</strong> $(date)</p>
        <p><strong>Test Type:</strong> $TEST_TYPE</p>
        <p><strong>Total Tests:</strong> $TOTAL_TESTS</p>
        <p class="success">Passed: $PASSED_TESTS</p>
        <p class="error">Failed: $FAILED_TESTS</p>
        <p class="info">Skipped: $SKIPPED_TESTS</p>
    </div>
    
    <h2>Test Details</h2>
    <p>Detailed test results are available in the individual XML reports in this directory.</p>
    
    <h2>Files</h2>
    <ul>
        <li><a href="../site/jacoco/index.html">Coverage Report</a></li>
        <li>XML Reports: TEST-*.xml files in this directory</li>
    </ul>
</body>
</html>
EOF
    
    print_success "Detailed reports generated in $REPORTS_DIR/"
fi

# Run security tests
print_step "Running security analysis..."

SECURITY_OUTPUT=$(mvn spotbugs:check $TEST_OPTS 2>&1)
SECURITY_EXIT_CODE=$?

if [ $SECURITY_EXIT_CODE -eq 0 ]; then
    print_success "Security analysis passed"
else
    print_error "Security issues detected"
    if [ "$VERBOSE" = true ]; then
        echo "$SECURITY_OUTPUT"
    fi
fi

# Calculate execution time
END_TIME=$(date +%s)
EXECUTION_TIME=$((END_TIME - START_TIME))

# Display test summary
echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}          TEST SUMMARY                  ${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

echo -e "🧪 ${GREEN}Test Type:${NC} $TEST_TYPE"
echo -e "⏱️  ${GREEN}Execution Time:${NC} ${EXECUTION_TIME}s"
echo -e "📊 ${GREEN}Total Tests:${NC} $TOTAL_TESTS"
echo -e "✅ ${GREEN}Passed:${NC} $PASSED_TESTS"
echo -e "❌ ${GREEN}Failed:${NC} $FAILED_TESTS"
echo -e "⏭️  ${GREEN}Skipped:${NC} $SKIPPED_TESTS"

if [ "$COVERAGE" = true ] && [ -n "$COVERAGE_PERCENT" ]; then
    echo -e "📈 ${GREEN}Coverage:${NC} $COVERAGE_PERCENT"
fi

echo ""

# Overall result
if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}✅ All tests passed successfully!${NC}"
    EXIT_CODE=0
else
    echo -e "${RED}❌ Some tests failed!${NC}"
    EXIT_CODE=1
fi

echo ""

# Display available reports
if [ "$COVERAGE" = true ] || [ "$REPORTS" = true ]; then
    echo -e "${BLUE}Available Reports:${NC}"
    
    if [ "$COVERAGE" = true ] && [ -f "$COVERAGE_DIR/index.html" ]; then
        echo -e "  • Coverage: ${YELLOW}$COVERAGE_DIR/index.html${NC}"
    fi
    
    if [ "$REPORTS" = true ] && [ -f "$REPORTS_DIR/test-summary.html" ]; then
        echo -e "  • Test Summary: ${YELLOW}$REPORTS_DIR/test-summary.html${NC}"
    fi
    
    echo ""
fi

# Display next steps
echo -e "${BLUE}Next Steps:${NC}"
if [ $FAILED_TESTS -gt 0 ]; then
    echo -e "  • Review failed tests: ${YELLOW}mvn test -Dtest=FailedTestName${NC}"
    echo -e "  • Check logs: ${YELLOW}target/surefire-reports/TEST-*.xml${NC}"
fi
echo -e "  • View coverage: ${YELLOW}open $COVERAGE_DIR/index.html${NC}"
echo -e "  • Run specific test: ${YELLOW}mvn test -Dtest=TestClassName${NC}"
echo ""

exit $EXIT_CODE
