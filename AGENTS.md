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

1. Avoid calling the same PSI getter repeatedly. Store intermediate PSI results in local variables instead of recomputing them.
2. Avoid expensive `PsiElement` methods unless necessary:
   - Prefer `textMatches()` over `getText()` when comparing text.
   - Prefer `getTextLength()` over `getTextRange()` when only the length is needed.
   - Avoid repeated calls to `getContainingFile()` and `getProject()`; compute them once and reuse them.
3. Minimize AST loading. Do not force AST access through methods such as `getText()`, `getNode()`, or `getTextRange()` unless required.
4. Avoid loading many PSI trees or documents into memory at the same time. Process files incrementally and do not keep strong references longer than needed.
5. Prefer stubs, indexes, or gists instead of loading full PSI trees when possible.
6. Cache expensive computations that are reused, such as resolve results, type evaluation, or inference.
7. If caching depends only on the current PSI subtree, prefer local PSI-level caching and invalidate it on subtree changes.
8. If cached values depend on indexes and also use `ProjectRootManager` as a dependency, add `DumbService` as a dependency as well.
9. Ensure tests and diagnostics do not accidentally trigger AST loading where it should be avoided. :contentReference[oaicite:0]{index=0}

## Writing short and clear rules

1. Use simple constructions:
   - Prefer present tense.
   - Use one idea per sentence.
   - Avoid passive voice.
2. Remove generic words such as `general`, `advanced`, `options`, or vague labels like `Learn more` unless they add real information.
3. Remove obvious objects and actions:
   - Do not explain what a control already implies.
   - Avoid redundant verbs like `click`, `specify`, `allow`, or `prefer` when the UI element already expresses that meaning.
4. Do not address the user directly when it adds no value. Avoid phrases like `you can` or `when you...` if the sentence works without them.
5. Remove duplicate words and duplicate meaning:
   - Move repeated context words to a shared heading when possible.
   - Do not repeat the same idea in both title and description.
6. Translate from technical language to user language:
   - Write from the user’s point of view.
   - Avoid implementation details, internal logic, and jargon unless they are necessary for understanding.
7. Write for first-time users:
   - Assume the reader sees the text for the first time.
   - Make labels and descriptions unambiguous without requiring prior context.
8. Prefer concise, informative labels and messages over longer explanatory text.
9. Remove words that do not change meaning.
10. Every UI text should be short, clear, specific, and immediately understandable. :contentReference[oaicite:1]{index=1}

## Summary for verification development

1. All inspections must be registered and covered by tests.
2. All inspections must leverage Kotlin and IDE capabilities effectively.
3. All reusable helper utilities should be covered by JUnit 3 tests.
4. All inspections must cover corner cases and include extended test scenarios.
5. All inspections must follow PSI performance rules and avoid unnecessary AST, PSI, and document loading.
6. All user-facing inspection texts must be short, clear, human-readable, and free of redundant wording. :contentReference[oaicite:2]{index=2}