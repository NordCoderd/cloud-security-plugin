# Cloud (IaC) Security

[![CI](https://github.com/NordCoderd/infrastructure-security/actions/workflows/gradle.yml/badge.svg)](https://github.com/NordCoderd/infrastructure-security/actions/workflows/gradle.yml)
[![JetBrains Plugin Version](https://img.shields.io/jetbrains/plugin/v/dev.protsenko.security-linter)](https://plugins.jetbrains.com/plugin/25413-infrastructure-security)
[![JetBrains Plugin Downloads](https://img.shields.io/jetbrains/plugin/d/dev.protsenko.security-linter)](https://plugins.jetbrains.com/plugin/25413-infrastructure-security)

<!-- Plugin description -->
Cloud (IaC) Security Linter for JetBrains IDEs (e.g., IntelliJ IDEA, PyCharm, WebStorm, and more).

Scan Docker, Kubernetes, and other Infrastructure-as-Code (IaC) files for security vulnerabilities and misconfigurations directly within your JetBrains IDE.

## Why this plugin?

- Seamless integration into the IDE without installing external tools.
- Verifies your files on the fly and highlight problems earlier, and that make shift left happens.
- Quick-fixes for problems are available for some inspections that could help fix problems faster.
- Supports complicated verifications, such as tracking variables and arguments as sources of issues.
- Pure Kotlin implementation, leveraging the power of IDEs.

## What does the plugin offer?

- **Dockerfile Analysis**: Detect security vulnerabilities and optimize Docker images with over 40 checks.
- **Docker Compose**: Detect security vulnerabilities and misconfigurations.
- **Kubernetes**: Detect security issues to align with the NSA Kubernetes Hardening Guide [wip].
- **Quick Fixes**: Resolve issues faster using built-in quick fixes.

## What problems can the plugin detect?

You can find more information about detected problems:

- [Documentation site](https://protsenko.dev/infrastructure-security)
- [Exciting story about creating the plugin](https://protsenko.dev/2025/03/24/how-i-made-docker-linter-for-intellij-idea-and-other-jetbrains-ide/)
- In-IDE pop-up messages describing each issue, each of which links to a dedicated article in the documentation

## Planned features

- **Kubernetes**: Implementing rules to align with the NSA Kubernetes Hardening Guide.
- **and more**: Expanding support for other IaC tools and formats to comprehensively protect and optimize your infrastructure configurations.

## Thanks

- My mother, who supported me every step of the way and who is no longer with us.
- [Trivy-checks](https://github.com/aquasecurity/trivy-checks/tree/main) for a good source of rules.
- [Hadolint](https://github.com/hadolint/hadolint) for yet another docker rule set.
- [Kubescape regorules](https://github.com/kubescape/regolibrary) for a good source of Kubernetes rules.
<!-- Plugin description end -->
