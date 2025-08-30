package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.RunCommandValidator

object ApkNoCacheValidator : RunCommandValidator {
    private val VALID_COMMAND_PATTERN =
        Regex(
            "^\\s*RUN(?:(?!.*apk add)|(?=.*--no-cache))(?!.*apk update)(?!.*rm\\s+.*?/var/cache/apk/\\*).*$",
            RegexOption.DOT_MATCHES_ALL,
        )

    override fun isValid(command: String): Boolean = VALID_COMMAND_PATTERN.matches(command)
}
