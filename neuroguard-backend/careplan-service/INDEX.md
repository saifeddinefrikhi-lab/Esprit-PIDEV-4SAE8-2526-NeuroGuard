# 📚 CarePlan Service - JaCoCo Documentation Index

## 🚀 START HERE

**👉 First time?** Read this in order:

1. [README_JACOCO_SETUP.txt](README_JACOCO_SETUP.txt) - Visual overview (2 min)
2. [JACOCO_QUICKSTART.md](JACOCO_QUICKSTART.md) - Quick commands (5 min)
3. Run your first test: `mvn clean test` (2 min)

---

## 📖 Complete Documentation

### 🟢 For Everyone (Start Here)

| Document | Purpose | Read Time |
|----------|---------|-----------|
| [README_JACOCO_SETUP.txt](README_JACOCO_SETUP.txt) | 📋 Overview & quick reference | 2 min |
| [JACOCO_QUICKSTART.md](JACOCO_QUICKSTART.md) | ⚡ Essential commands & scripts | 5 min |

### 🔵 For Developers (Writing Tests)

| Document | Purpose | Read Time |
|----------|---------|-----------|
| [JACOCO_TEST_GUIDE.md](JACOCO_TEST_GUIDE.md) | 📚 Complete guide with details | 15 min |
| [TESTING_BEST_PRACTICES.md](TESTING_BEST_PRACTICES.md) | 🎓 Testing patterns & examples | 20 min |
| [REPORT_INTERPRETATION.md](REPORT_INTERPRETATION.md) | 📊 How to read reports | 10 min |

### 🟣 For DevOps/Architects (Advanced)

| Document | Purpose | Read Time |
|----------|---------|-----------|
| [JACOCO_ADVANCED_CONFIG.md](JACOCO_ADVANCED_CONFIG.md) | 🔧 Advanced configurations | 20 min |

---

## 🎯 Quick Navigation by Task

### "I want to run tests right now"
```bash
mvn clean test
# then open: target/jacoco-reports/index.html
```
→ See [JACOCO_QUICKSTART.md](JACOCO_QUICKSTART.md)

### "I need to improve test coverage"
→ Read [TESTING_BEST_PRACTICES.md](TESTING_BEST_PRACTICES.md)

### "How do I read the coverage report?"
→ See [REPORT_INTERPRETATION.md](REPORT_INTERPRETATION.md)

### "I need to configure stricter coverage requirements"
→ Check [JACOCO_ADVANCED_CONFIG.md](JACOCO_ADVANCED_CONFIG.md)

### "I'm new to JaCoCo, where do I start?"
→ Follow this order:
1. [README_JACOCO_SETUP.txt](README_JACOCO_SETUP.txt)
2. [JACOCO_QUICKSTART.md](JACOCO_QUICKSTART.md)
3. [JACOCO_TEST_GUIDE.md](JACOCO_TEST_GUIDE.md)

### "Something is broken, how do I debug?"
→ See troubleshooting in [JACOCO_TEST_GUIDE.md](JACOCO_TEST_GUIDE.md)

---

## 🛠️ Automation Scripts

### Windows
```bash
run-tests-jacoco.bat
```
Interactive menu with 6 options:
1. Run tests with JaCoCo report
2. Run tests and open report
3. Run specific test with coverage
4. Clean and run full test suite
5. Skip coverage checks (fast test)
6. Exit

### Linux/Mac
```bash
chmod +x run-tests-jacoco.sh
./run-tests-jacoco.sh
```
Same 6 options as Windows version

---

## 📋 File Structure

```
careplan-service/
├── pom.xml                          ✅ JaCoCo configured
├── README_JACOCO_SETUP.txt          📋 Visual overview
├── JACOCO_QUICKSTART.md             ⚡ Quick commands
├── JACOCO_TEST_GUIDE.md             📚 Complete guide
├── JACOCO_ADVANCED_CONFIG.md        🔧 Advanced options
├── REPORT_INTERPRETATION.md         📊 Report reading
├── TESTING_BEST_PRACTICES.md        🎓 Testing patterns
├── INDEX.md                         📑 This file
├── run-tests-jacoco.bat             🪟 Windows launcher
├── run-tests-jacoco.sh              🐧 Linux/Mac launcher
├── src/test/java/                   ✅ 5 test classes
│   ├── CarePlanServiceTest.java
│   ├── StatisticsServiceTest.java
│   ├── RiskAnalysisServiceTest.java
│   ├── PrescriptionServiceTest.java
│   └── CareplanServiceApplicationTests.java
└── target/                          (after running tests)
    ├── jacoco.exec                  Raw coverage data
    └── jacoco-reports/              📊 HTML reports
```

---

## ✅ Current Configuration

| Setting | Value |
|---------|-------|
| **Plugin** | JaCoCo Maven Plugin 0.8.10 |
| **Build Tool** | Maven 3.x |
| **Java Version** | 17 |
| **Test Framework** | JUnit 5 |
| **Mock Library** | Mockito |
| **Min Coverage** | 50% (line coverage) |
| **Report Format** | HTML, XML, CSV |

---

## 🚀 Quick Commands Cheat Sheet

```bash
# Basic test run
mvn clean test

# Run specific test
mvn test -Dtest=CarePlanServiceTest

# Skip coverage checks
mvn clean test -Djacoco.skip=true

# Generate XML report (for CI/CD)
mvn clean test jacoco:report

# Open report
start target/jacoco-reports/index.html  # Windows
open target/jacoco-reports/index.html   # Mac
xdg-open target/jacoco-reports/index.html # Linux
```

---

## 📊 What to Expect

### After running `mvn clean test`:

1. **Test execution:** ~10-30 seconds (depends on test count)
2. **Coverage collection:** Automatic via JaCoCo agent
3. **Report generation:** Automatic
4. **Output files:**
   - HTML report: `target/jacoco-reports/index.html`
   - Raw data: `target/jacoco.exec`
   - XML report: `target/jacoco-reports/jacoco.xml`

### Report contents:
- 📈 Coverage percentage (line, branch, instruction)
- 🎨 Color-coded source code (green=covered, red=not covered)
- 📦 Package-by-package breakdown
- 🔍 Method complexity analysis
- 📋 Summary statistics

---

## 🎓 Learning Resources

### Internal Documentation
- This INDEX.md file
- All .md files in this folder

### External Resources
- [JaCoCo Official Documentation](https://www.jacoco.org/jacoco)
- [JUnit 5 User Guide](https://junit.org/junit5/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest)

---

## 🆘 Getting Help

### Common Issues

| Problem | Solution |
|---------|----------|
| "Report not generated" | See [JACOCO_TEST_GUIDE.md](JACOCO_TEST_GUIDE.md#-dépannage) |
| "Coverage too low" | Read [TESTING_BEST_PRACTICES.md](TESTING_BEST_PRACTICES.md) |
| "Maven not found" | Install Maven and add to PATH |
| "Tests failing" | Run with `-X` flag: `mvn clean test -X` |

### Getting Support
1. Check the troubleshooting section in [JACOCO_TEST_GUIDE.md](JACOCO_TEST_GUIDE.md)
2. Review [TESTING_BEST_PRACTICES.md](TESTING_BEST_PRACTICES.md) for test patterns
3. Check your specific error in the documentation

---

## 📞 Quick Reference

**What is JaCoCo?**  
Tool that measures what percentage of your code is executed by tests

**Why is it important?**  
Higher coverage = fewer bugs, better code quality

**Current requirement:**  
Minimum 50% line coverage (prevents accidental code that's never tested)

**Main goal:**  
Achieve 70%+ coverage in critical services

---

## 🎯 Next Steps

### 👶 Beginner
1. Run: `mvn clean test`
2. Open: `target/jacoco-reports/index.html`
3. Read: [REPORT_INTERPRETATION.md](REPORT_INTERPRETATION.md)

### 👨‍💼 Intermediate
1. Review existing tests
2. Follow patterns in [TESTING_BEST_PRACTICES.md](TESTING_BEST_PRACTICES.md)
3. Add tests to improve coverage

### 🏆 Advanced
1. Configure stricter rules: [JACOCO_ADVANCED_CONFIG.md](JACOCO_ADVANCED_CONFIG.md)
2. Integrate with CI/CD pipeline
3. Set up SonarQube integration

---

## 📝 Document Versions

| Document | Version | Last Updated |
|----------|---------|-------------|
| INDEX.md | 1.0 | 2026-05-06 |
| README_JACOCO_SETUP.txt | 1.0 | 2026-05-06 |
| JACOCO_QUICKSTART.md | 1.0 | 2026-05-06 |
| JACOCO_TEST_GUIDE.md | 1.0 | 2026-05-06 |
| TESTING_BEST_PRACTICES.md | 1.0 | 2026-05-06 |
| REPORT_INTERPRETATION.md | 1.0 | 2026-05-06 |
| JACOCO_ADVANCED_CONFIG.md | 1.0 | 2026-05-06 |

---

## 📞 Support

For questions or issues:
1. Check this INDEX.md for navigation
2. Search relevant documents
3. Review troubleshooting sections
4. Check JaCoCo official docs

---

**Happy Testing! 🚀**

Last Updated: 2026-05-06  
JaCoCo Version: 0.8.10
