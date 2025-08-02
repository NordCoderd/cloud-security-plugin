package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.RunCommandValidator

object DistUpgradeValidator : RunCommandValidator {
    private val pattern =
        Regex(
            pattern = """\b(apt-get|zypper)\b.*\b(dist-upgrade|dup)\b""",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL),
        )

    override fun isValid(command: String): Boolean = !pattern.containsMatchIn(command)
}
