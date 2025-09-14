package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.RunCommandValidator

object PipNoCacheDirValidator : RunCommandValidator {
    /**
     * Regular expression to detect pip install commands that lack the --no-cache-dir option.
     * Pattern explanation:
     * - ^RUN\s+ : Command must start with 'RUN' followed by one or more whitespace characters
     * - (?=.*pip\s+install) : Positive lookahead to ensure 'pip install' is present somewhere
     * - (?!.*--no-cache-dir) : Negative lookahead to ensure '--no-cache-dir' is NOT present anywhere
     * - .* : Match any remaining characters including newlines and special symbols
     * - $ : End of string anchor
     */
    private val regex =
        Regex(
            "^RUN\\s+(?=.*pip\\s+install)(?!.*--no-cache-dir).*$",
            RegexOption.DOT_MATCHES_ALL,
        )

    /**
     * Validates if a Docker RUN command follows security best practices for pip install.
     *
     * @param command The Docker command string to validate
     * @return false if pip install is used without --no-cache-dir (security violation), true otherwise
     */
    override fun isValid(command: String): Boolean = !regex.matches(command)
}
