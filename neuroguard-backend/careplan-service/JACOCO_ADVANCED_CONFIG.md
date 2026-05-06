# 🎚️ JaCoCo Advanced Configuration Options

This file documents advanced JaCoCo configuration options you can add to `pom.xml` for stricter coverage requirements.

## 📋 Current Configuration (Basic)

**File:** `pom.xml`  
**Status:** ✅ Already configured  
**Coverage Requirement:** 50% minimum (line coverage, all packages)

---

## 🔧 Advanced Configuration Options

### Option 1: Stricter Global Coverage (70%)

Replace the `check` execution in `pom.xml` with:

```xml
<!-- In jacoco-maven-plugin > executions -->
<execution>
    <id>check</id>
    <phase>test</phase>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <excludes>
                    <exclude>*Test</exclude>
                    <exclude>*.dto</exclude>
                    <exclude>*.config</exclude>
                </excludes>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.70</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

**Effect:** Build fails if coverage < 70%

---

### Option 2: Per-Package Coverage Rules

```xml
<execution>
    <id>check</id>
    <phase>test</phase>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <!-- Services: 80% minimum -->
            <rule>
                <element>PACKAGE</element>
                <includes>
                    <include>com.esprit.microservice.careplanservice.services.*</include>
                </includes>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
            
            <!-- Controllers: 60% minimum -->
            <rule>
                <element>PACKAGE</element>
                <includes>
                    <include>com.esprit.microservice.careplanservice.controllers.*</include>
                </includes>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.60</minimum>
                    </limit>
                </limits>
            </rule>
            
            <!-- Others: 40% minimum -->
            <rule>
                <element>PACKAGE</element>
                <excludes>
                    <exclude>*Test</exclude>
                    <exclude>*.dto</exclude>
                    <exclude>*.config</exclude>
                    <exclude>*.services.*</exclude>
                    <exclude>*.controllers.*</exclude>
                </excludes>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.40</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

---

### Option 3: Branch & Line Coverage Combined

```xml
<execution>
    <id>check</id>
    <phase>test</phase>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <excludes>
                    <exclude>*Test</exclude>
                </excludes>
                <limits>
                    <!-- Line coverage -->
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.65</minimum>
                    </limit>
                    <!-- Branch coverage -->
                    <limit>
                        <counter>BRANCH</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.60</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

**Metrics:**
- **COVEREDRATIO:** Percentage of items covered
- **MISSEDCOUNT:** Number of items not covered
- **LINE, BRANCH, INSTRUCTION:** Different coverage types

---

### Option 4: Class-Level Coverage Requirements

```xml
<execution>
    <id>check</id>
    <phase>test</phase>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <!-- Each class must have 50% coverage -->
            <rule>
                <element>CLASS</element>
                <excludes>
                    <exclude>*Test</exclude>
                    <exclude>*.dto.*</exclude>
                    <exclude>*.entity.*</exclude>
                </excludes>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.50</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

---

### Option 5: Complex Rules with Missed Count

```xml
<execution>
    <id>check</id>
    <phase>test</phase>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <includes>
                    <include>com.esprit.microservice.careplanservice.services.*</include>
                </includes>
                <limits>
                    <!-- At least 70% coverage -->
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.70</minimum>
                    </limit>
                    <!-- Or: no more than 50 uncovered lines -->
                    <limit>
                        <counter>LINE</counter>
                        <value>MISSEDCOUNT</value>
                        <maximum>50</maximum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

---

## 📊 Counter Types

| Counter | Description | Example |
|---------|------------|---------|
| **INSTRUCTION** | Individual bytecode instructions | `int x = 5;` |
| **BRANCH** | Decision points (if/else, switch) | `if (x > 0)` |
| **LINE** | Source code lines | Entire method |
| **COMPLEXITY** | Cyclomatic complexity | Method paths |
| **METHOD** | Method definitions | `public void foo()` |
| **CLASS** | Class definitions | `public class Foo` |

---

## 📈 Value Types

| Value | Description |
|-------|------------|
| **COVEREDRATIO** | Percentage of covered items (0.0-1.0) |
| **MISSEDCOUNT** | Absolute number of uncovered items |

---

## 🎯 Recommended Profiles

Create different profiles for different environments:

```xml
<profiles>
    <!-- Development: relaxed requirements -->
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <!-- Use current 50% requirement -->
    </profile>
    
    <!-- CI/CD: strict requirements -->
    <profile>
        <id>ci</id>
        <!-- Require 70% coverage -->
    </profile>
    
    <!-- Release: very strict -->
    <profile>
        <id>release</id>
        <!-- Require 80% coverage -->
    </profile>
</profiles>
```

Then run:
```bash
mvn clean test -Pci      # Use CI profile (70%)
mvn clean test -Prelease # Use release profile (80%)
mvn clean test            # Use dev profile (50%)
```

---

## 🚨 Failure Behavior

### When Coverage Check Fails

```bash
$ mvn clean test

[ERROR] Build failure in module 'careplan-service'
[ERROR] Coverage check failed!
[ERROR] - com.esprit...services: 48% (required: 50%)
```

### Options to Fix

1. **Add more tests** (recommended)
   ```bash
   # Improve coverage and re-run
   mvn clean test
   ```

2. **Temporarily disable check**
   ```bash
   mvn clean test -Djacoco.skip=true
   ```

3. **Lower the requirement**
   - Edit `pom.xml`: change `<minimum>0.50</minimum>` to lower value
   - Note: Not recommended for production

---

## 🔗 SonarQube Integration

If using SonarQube for quality gates:

```xml
<!-- In properties -->
<properties>
    <sonar.coverage.jacoco.xmlReportPaths>
        target/jacoco-reports/jacoco.xml
    </sonar.coverage.jacoco.xmlReportPaths>
    <sonar.java.coveragePlugin>jacoco</sonar.java.coveragePlugin>
    <sonar.dynamicAnalysis>reuseReports</sonar.dynamicAnalysis>
</properties>
```

---

## 📚 Common Configurations by Team Size

### Small Team (Start with this)
- ✅ 40% minimum coverage
- ✅ Simple rules
- ✅ Current configuration

### Growing Team
- ✅ 60% minimum coverage
- ✅ Services required 75%
- ✅ Per-package rules

### Enterprise
- ✅ 80% minimum coverage
- ✅ Class-level validation
- ✅ Complexity limits
- ✅ Multiple profiles

---

## ⚡ Commands for Different Configurations

```bash
# View current configuration
cat pom.xml | grep -A 20 "check"

# Skip all checks (development)
mvn clean test -Djacoco.skip=true

# Verbose output for debugging
mvn clean test -Djacoco.haltOnFailure=false

# Generate XML report for external tools
mvn clean test jacoco:report
```

---

## 📖 Documentation

- [JaCoCo Maven Plugin Documentation](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [Coverage Counters Documentation](https://www.jacoco.org/jacoco/trunk/doc/counters.html)
- [Rule Definition Reference](https://www.jacoco.org/jacoco/trunk/doc/check-mojo.html)

---

## 🎯 Recommended: Start Conservative, Increase Over Time

```
Week 1-2:  50% (current) → Focus on writing tests
Week 3-4:  55% → Improve critical services
Month 2:   65% → Add edge case tests
Month 3+:  70%+ → Maintain high quality
```

---

**Last Updated:** 2026-05-06  
**JaCoCo Version:** 0.8.10
