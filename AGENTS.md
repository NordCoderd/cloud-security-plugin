## Project overview

IntelliJ Platform plugin "Cloud (IaC) Security": security inspections for Dockerfile, Docker Compose, Kubernetes and other YAML-based IaC files. Pure Kotlin, built with Gradle (Kotlin DSL).

- Inspections: [securityLinter](src/main/kotlin/dev/protsenko/securityLinter), one folder per technology (`docker`, `dockerCompose`, `kubernetes`, `core`, `utils`).
- Resources: [messages](src/main/resources/messages), [inspectionDescriptions](src/main/resources/inspectionDescriptions), [META-INF](src/main/resources/META-INF).
- Tests and test data: [src/test/kotlin](src/test/kotlin), [src/test/testData](src/test/testData).

## Commands

- Run all tests: `./gradlew test`
- Full build: `./gradlew build`
- Prefer the narrowest check while iterating (single test class, e.g. `./gradlew test --tests "*DS003*"`); run the full suite before finishing.

## General rules
1. Add only new files to git. Do not modify the existing staging unless explicitly required.
2. Do not commit or push unless explicitly asked.
3. Before adding code, look at a sibling inspection in the same technology folder and copy its structure, naming and comment density.
4. Reuse existing helpers (`RunCommandValidator`, `YamlPath`, shared checkers) before writing new ones.
5. Keep changes scoped to the task. No drive-by refactors, no unrelated formatting changes.
6. Never edit generated or local-only paths: `build/`, `.intellijPlatform/`, `.gradle/`.
7. Never hardcode user-facing strings in Kotlin; use `SecurityPluginBundle.properties`.
8. Report test and build results faithfully. If a step was skipped or failed, say so.

## Verification development stages

1. Implement everything in pure Kotlin, making full use of IDE-assisted language features and inspections.
2. Add inspection messages and tooltips to [SecurityPluginBundle.properties](src/main/resources/messages/SecurityPluginBundle.properties).
3. Create the inspection draft in the technology-specific folder under [securityLinter](src/main/kotlin/dev/protsenko/securityLinter).
4. Add tests covering the inspection:
   - Example of a Docker inspection: [DS003SshPortExposed.kt](src/test/kotlin/dev/protsenko/securityLinter/docker/DS003SshPortExposed.kt)
   - Example of test data for Docker inspections: [DS003](src/test/testData/docker/DS003)
   - Example of extended test data with additional cases: [DS029](src/test/testData/docker/DS029)
5. Create an inspection description in HTML using the same filename as the inspection class in [inspectionDescriptions](src/main/resources/inspectionDescriptions).
6. Register the inspection in XML:
   - For YAML-based inspections: [dev.protsenko.security-linter-yaml.xml](src/main/resources/META-INF/dev.protsenko.security-linter-yaml.xml)
   - For general and Docker-based inspections: [plugin.xml](src/main/resources/META-INF/plugin.xml)
7. If the inspection validates Docker RUN commands, follow the rules defined in [RunCommandValidator.kt](src/main/kotlin/dev/protsenko/securityLinter/docker/checker/core/RunCommandValidator.kt).
   - Register the required extension points in [plugin.xml](src/main/resources/META-INF/plugin.xml).
8. If the inspection is YAML-based, use the bundled helper classes such as [YamlPath.kt](src/main/kotlin/dev/protsenko/securityLinter/utils/YamlPath.kt).
9. Ensure that all tests pass.
10. If the plugin version has not been updated in [gradle.properties](gradle.properties), update it.
11. Update the changelog in [CHANGELOG.md](CHANGELOG.md).
12. Perform a full build and run the complete test suite for the plugin.

## Summary for verification development

1. All inspections must be registered and covered by tests.
2. All inspections must leverage Kotlin and IDE capabilities effectively.
3. All reusable helper utilities should be covered by JUnit 3 tests.
4. All inspections must cover corner cases and include extended test scenarios.