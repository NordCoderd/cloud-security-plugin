# Infrastructure as Code (IaC) Security Linter

[![CI](https://github.com/NordCoderd/infrastructure-security/actions/workflows/gradle.yml/badge.svg)](https://github.com/NordCoderd/infrastructure-security/actions/workflows/gradle.yml)
[![JetBrains Plugin Version](https://img.shields.io/jetbrains/plugin/v/dev.protsenko.security-linter)](https://plugins.jetbrains.com/plugin/25413-infrastructure-security)
[![JetBrains Plugin Downloads](https://img.shields.io/jetbrains/plugin/d/dev.protsenko.security-linter)](https://plugins.jetbrains.com/plugin/25413-infrastructure-security)

<!-- Plugin description -->
Infrastructure as Code (IaC) Security Linter for JetBrains IDEs (e.g., IntelliJ IDEA, PyCharm, WebStorm, and more).

Scan Docker and Infrastructure as Code (IaC) files for security vulnerabilities and misconfigurations directly in your JetBrains IDE.

## Why this plugin?

- Seamless integration into IDE without installing external tools.
- Verifies your files on the fly and highlight problems earlier and that make shift left happens.
- Quick-fixes for problems are available for some inspections that could help fix problem faster.
- Supports complicated verifications, such as tracking variables and arguments as sources of issues.
- Pure Kotlin implementation, leveraging the power of IDEs.
- Because I made it with love, [read about my experience creating the plugin.](https://protsenko.dev/2025/03/24/how-i-made-docker-linter-for-intellij-idea-and-other-jetbrains-ide/)

## What does the plugin offer?

- **Dockerfile Analysis**: Detect security vulnerabilities and optimize Docker images with over 40 checks.
- **Docker Compose**: Detect security vulnerabilities and misconfigurations.
- **Quick Fixes**: Resolve issues faster using built-in quick fixes.

## What problems could find that plugin?

You could find more information about detected problems:

- [Documentation](https://protsenko.dev/infrastructure-security)
- In IDE popup messages with problem. Problem messages have a link to dedicated article in the documentation.

## Planned features

- **Extended support for Dockerfile and docker-compose files**
- **Kubernetes Files**: Analyzing Kubernetes YAML files to comply with best practices and security standards.
- **and more**: Expanding support for other IaC tools and formats to comprehensively protect and optimize your infrastructure configurations.

Detailed list of planned features are available on [GitHub issues](https://github.com/NordCoderd/infrastructure-security/labels/enhancement)

## Thanks
- My mother, who supported me every step of the way and who is no longer with us.
- [Trivy-checks](https://github.com/aquasecurity/trivy-checks/tree/main) for good source of rules.
- [Hadolint](https://github.com/hadolint/hadolint) for yet another docker rule set.
<!-- Plugin description end -->
