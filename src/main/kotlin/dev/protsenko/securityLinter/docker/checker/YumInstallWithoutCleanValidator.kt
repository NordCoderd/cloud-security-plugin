package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.RunCommandValidator

object YumInstallWithoutCleanValidator : RunCommandValidator {
    private val yumInstallWithoutCleanRegex =
        Regex(
            pattern = "(?i)\\byum\\s+install\\b(?:(?!\\byum\\s+clean\\s+all\\b).)*$",
            options = setOf(RegexOption.DOT_MATCHES_ALL),
        )

    override fun isValid(command: String) = !yumInstallWithoutCleanRegex.containsMatchIn(command)
}
