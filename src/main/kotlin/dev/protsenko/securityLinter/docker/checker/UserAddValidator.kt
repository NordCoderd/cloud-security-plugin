package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.RunCommandValidator
import java.util.regex.Pattern

/**
 * Validator that ensures every command starts with RUN and follows shell scripting rules.
 * Additionally, it checks for "useradd" commands missing the "-l" or "--no-log-init" flag
 * when a high UID (>= 100000) is used, which can lead to large image sizes.
 */
object UserAddValidator : RunCommandValidator {
    private val userAddPattern =
        Pattern.compile(
            "\\buseradd\\b(?!.*(?:-l|--no-log-init))(?=.*-u\\s+(\\d{6,})).*",
        )

    override fun isValid(command: String): Boolean = !userAddPattern.matcher(command).find()
}
