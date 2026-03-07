package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.RunCommandValidator

object WgetProgressBarValidator : RunCommandValidator {
    private val wgetPattern = Regex("""\bwget\b""", RegexOption.IGNORE_CASE)
    private val separators = Regex("""\s*(?:&&|;|\n)\s*""")
    private val recommendedWgetOptionsPattern =
        Regex(
            pattern = """(?:^|\s)(--progress=dot:giga|--quiet|--no-verbose|-q(?:\S*)|-nv(?:\S*))(?=\s|$)""",
            options = setOf(RegexOption.IGNORE_CASE),
        )

    override fun isValid(command: String): Boolean {
        if (!wgetPattern.containsMatchIn(command)) return true

        val cmd = command.removePrefix("RUN").trim()
        val individualCommands = separators.split(cmd)

        for (individualCommand in individualCommands) {
            val cmdTrimmed = individualCommand.trim()
            if (!wgetPattern.containsMatchIn(cmdTrimmed)) continue
            if (!recommendedWgetOptionsPattern.containsMatchIn(cmdTrimmed)) {
                return false
            }
        }

        return true
    }
}
