package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.RunCommandValidator

object PackageManagerAutoYesValidator : RunCommandValidator {
    /**
     * Regular expression to decrease count of analyzed command
     */
    private val packageManagerPattern = Regex("""\b(apt-get|yum|dnf|zypper)\b""", RegexOption.IGNORE_CASE)

    /**
     * Regular expression to split commands using separators like '&&', ';', or newlines.
     */
    private val separators = Regex("""\s*(?:&&|;|\n)\s*""")

    /**
     * Regular expression to match the '-y' or equivalent flags.
     *
     * - Matches '-y', '--yes', '--assume-yes', '--assumeyes', '--non-interactive'.
     * - `-[^\s]*y[^\s]*`: Matches flags like '-qy' or '-yq'.
     */
    private val assumeYesFlagsPattern =
        Regex(
            pattern = """\s(-{1,2}(yes|assume-yes|assumeyes|non-interactive)\b|-[^\s]*y[^\s]*)""",
            options = setOf(RegexOption.IGNORE_CASE),
        )

    /**
     * Regular expression to match package manager install commands.
     *
     * - Matches commands starting with 'apt-get', 'yum', 'dnf', or 'zypper'.
     * - Allows for options before 'install'.
     */
    private val installCommandPattern =
        Regex(
            pattern = """^\s*(apt-get|yum|dnf|zypper)(?:\s+\S+)*\s+install\b""",
            options = setOf(RegexOption.IGNORE_CASE),
        )

    /**
     * Validates the RUN command to ensure that package manager install commands include '-y' or equivalent flags.
     *
     * @param command The RUN command string to validate.
     * @return `true` if the command is valid (all install commands include '-y' or equivalent), otherwise `false`.
     */
    override fun isValid(command: String): Boolean {
        // Check if the command contains any of the package managers
        if (!command.contains(packageManagerPattern)) return true

        // Remove 'RUN' prefix and trim whitespace
        val cmd = command.removePrefix("RUN").trim()

        // Split the command into individual commands based on separators
        val individualCommands = separators.split(cmd)

        // Check each individual command
        for (individualCommand in individualCommands) {
            val cmdTrimmed = individualCommand.trim()
            // If it's an install command
            if (installCommandPattern.containsMatchIn(cmdTrimmed)) {
                // Ensure that it includes '-y' or equivalent flag
                if (!assumeYesFlagsPattern.containsMatchIn(cmdTrimmed)) {
                    return false
                }
            }
        }
        return true
    }
}
