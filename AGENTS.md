## General rules
1. Add only new files to git. Do not modify the existing staging unless explicitly required.
2. Add new files to git after all changes are complete.

## Verification development stages

1. Implement everything in pure Kotlin, making full use of IDE-assisted language features and inspections.
2. Don't touch files with existing inspection implementations or their texts.
3. Add inspection messages and tooltips to [SecurityPluginBundle.properties](src/main/resources/messages/SecurityPluginBundle.properties). Make sure each message includes: the problem, why it is a problem, and the solution. Keep it very brief.
4. Create the inspection draft in the technology-specific folder under [securityLinter](src/main/kotlin/dev/protsenko/securityLinter).
5. The inspection implementation must be performant, simple, and able to handle thousands of changes within minutes.
6. Add tests covering the inspection:
   - Example of a Docker inspection: [DS003SshPortExposed.kt](src/test/kotlin/dev/protsenko/securityLinter/docker/DS003SshPortExposed.kt)
   - Example of test data for Docker inspections: [DS003](src/test/testData/docker/DS003)
   - Example of extended test data with additional cases: [DS029](src/test/testData/docker/DS029)
7. Create an inspection description in HTML using the same filename as the inspection class in [inspectionDescriptions](src/main/resources/inspectionDescriptions).
8. Register the inspection in XML:
   - For YAML-based inspections: [dev.protsenko.security-linter-yaml.xml](src/main/resources/META-INF/dev.protsenko.security-linter-yaml.xml)
   - For general and Docker-based inspections: [plugin.xml](src/main/resources/META-INF/plugin.xml)
9. If the inspection validates Docker `RUN` commands, follow the rules defined in [RunCommandValidator.kt](src/main/kotlin/dev/protsenko/securityLinter/docker/checker/core/RunCommandValidator.kt).
   - Register the required extension points in [plugin.xml](src/main/resources/META-INF/plugin.xml).
10. If the inspection is YAML-based, use the bundled helper classes such as [YamlPath.kt](src/main/kotlin/dev/protsenko/securityLinter/utils/YamlPath.kt).
11. Ensure that all tests pass.
12. Update the changelog in [CHANGELOG.md](CHANGELOG.md) for the WIP version.
13. If the plugin version has not been updated in [gradle.properties](gradle.properties), update it and mark it as WIP in the changelog.
14. Perform a full build and run the complete test suite for the plugin.

## PSI performance rules

1. Avoid calling the same PSI getter repeatedly. Store intermediate PSI results in local variables instead of recomputing them. Many PSI getters are not trivial field reads and may traverse PSI children or allocate collections on each call. :contentReference[oaicite:0]{index=0}
2. Avoid expensive `PsiElement` methods on deep trees unless truly necessary. In particular:
   - Prefer `textMatches()` over `getText()` when checking text.
   - Prefer `getTextLength()` over `getTextRange()` when only the length is needed.
   - Avoid repeated calls to `getContainingFile()` and `getProject()`; compute them once per task and reuse them. :contentReference[oaicite:1]{index=1}
3. Minimize AST loading. Do not force AST access through methods such as `getText()`, `getNode()`, or `getTextRange()` unless required by the implementation. Prefer PSI, stubs, indexes, or gists where possible. :contentReference[oaicite:2]{index=2}
4. Avoid loading many PSI trees or documents into memory at the same time. Process files incrementally and do not keep strong references longer than needed. Ideally, only editor-open files should keep AST or document state loaded. :contentReference[oaicite:3]{index=3}
5. Prefer stubs, indexes, or gists for data access when possible instead of parsing or loading full PSI trees for many files. This is especially important for inspections expected to run across large projects. :contentReference[oaicite:4]{index=4}
6. Cache results of expensive computations when reused. This includes references, resolve operations, type evaluation, inference, and similar heavy PSI-derived results. Use `CachedValue` / `CachedValuesManager` where appropriate. :contentReference[oaicite:5]{index=5}
7. If caching depends only on the current PSI subtree, prefer a local PSI-level cache and clear it on subtree changes rather than recomputing on every access. :contentReference[oaicite:6]{index=6}
8. When cached values depend on indexes and also use `ProjectRootManager` as a dependency, add `DumbService` as a dependency as well. :contentReference[oaicite:7]{index=7}
9. For tests and diagnostics, ensure the implementation does not accidentally load AST where it should not. Use the platform mechanisms recommended by JetBrains to detect accidental AST loading. :contentReference[oaicite:8]{index=8}

## Summary for verification development

1. All inspections must be registered and covered by tests.
2. All inspections must leverage Kotlin and IDE capabilities effectively.
3. All reusable helper utilities should be covered by JUnit 3 tests.
4. All inspections must cover corner cases and include extended test scenarios.
5. All inspections must follow PSI performance rules and avoid unnecessary AST, PSI, and document loading. :contentReference[oaicite:9]{index=9}