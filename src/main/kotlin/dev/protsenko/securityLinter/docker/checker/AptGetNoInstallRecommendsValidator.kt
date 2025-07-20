package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.RunCommandValidator

object AptGetNoInstallRecommendsValidator : RunCommandValidator {

    /**
     * Regular expression to match commands where `apt-get install` is used without `--no-install-recommends`.
     *
     * - `(?i)`: Enables case-insensitive matching.
     * - `^RUN\s+`: Command starts with "RUN" followed by whitespace.
     * - `.*\bapt-get\s+install\b`: Matches "apt-get install" as whole words.
     * - `(?:(?!--no-install-recommends).)*$`: Negative lookahead to ensure "--no-install-recommends" does not appear anywhere after "apt-get install".
     */
    private val aptGetInstallWithoutNoRecommendsRegex = Regex(
        pattern = "(?i)^RUN\\s+.*\\bapt-get\\b(?:(?!--no-install-recommends).)*\\binstall\\b(?:(?!--no-install-recommends).)*$",
        options = setOf(RegexOption.DOT_MATCHES_ALL)
    )

    /**
     * Validates the RUN command to ensure that `apt-get install` is used with `--no-install-recommends`.
     *
     * @param command The RUN command string to validate.
     * @return `true` if the command is valid (does not use `apt-get install` without `--no-install-recommends`),
     *         otherwise `false`.
     */
    override fun isValid(command: String): Boolean {
        return !aptGetInstallWithoutNoRecommendsRegex.matches(command)
    }
}

