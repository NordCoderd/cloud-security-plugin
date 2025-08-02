package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.RunCommandValidator

object CurlBashingValidator : RunCommandValidator {
    val curlBashingRegex = Regex("RUN.*(curl|wget)[^|^>]*[|>]")

    override fun isValid(command: String): Boolean = !curlBashingRegex.containsMatchIn(command)
}
