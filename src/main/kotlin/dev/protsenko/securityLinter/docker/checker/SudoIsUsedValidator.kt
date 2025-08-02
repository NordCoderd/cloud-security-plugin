package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.RunCommandValidator

object SudoIsUsedValidator : RunCommandValidator {
    private val regex = Regex("\\bsudo\\b")

    override fun isValid(command: String): Boolean = !regex.containsMatchIn(command)
}
