package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.RunCommandValidator

object CurlBashingValidator : RunCommandValidator {
    val curlBashingRegex =
        Regex("""RUN\s+(?s:.*)(curl|wget)\s+(?s:.*)[|>]+\s*(bash|sh|zsh|ksh|tcsh|csh|dash|ash)\b""", RegexOption.IGNORE_CASE)

    override fun isValid(command: String): Boolean = !curlBashingRegex.containsMatchIn(command)
}
