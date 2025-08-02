package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.RunCommandValidator

object AptIsUsedValidator : RunCommandValidator {
    private val regexApt = Regex("\\bapt\\b")
    private val regexExceptions = Regex("\\bapt-(get|cache)\\b")

    override fun isValid(command: String): Boolean {
        if (!regexApt.containsMatchIn(command)) {
            return true
        }
        if (regexExceptions.containsMatchIn(command)) {
            return true
        }
        return false
    }
}
