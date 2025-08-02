package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.RunCommandValidator

object UpdateWithoutInstallValidator : RunCommandValidator {
    private val UPDATE_COMMANDS = setOf("update", "up")
    private val INSTALL_COMMANDS =
        setOf(
            "upgrade",
            "install",
            "source-install",
            "reinstall",
            "groupinstall",
            "localinstall",
            "add",
        )
    private val PACKAGE_MANAGERS = setOf("apt-get", "apt", "yum", "apk", "dnf", "zypper")

    private val packageManagersPattern = PACKAGE_MANAGERS.joinToString("|") { "\\b${Regex.escape(it)}\\b" }
    private val updateCommandsPattern = UPDATE_COMMANDS.joinToString("|") { "\\b${Regex.escape(it)}\\b(?![\\w-])" }
    private val installCommandsPattern = INSTALL_COMMANDS.joinToString("|") { "\\b${Regex.escape(it)}\\b" }

    private val pattern =
        Regex(
            pattern = """^(RUN\s+)?($packageManagersPattern)\s+($updateCommandsPattern)(?!.*\b\2\b(?:\s+\S+)*?\s+($installCommandsPattern)\b)""",
            options = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE),
        )

    override fun isValid(command: String): Boolean = !pattern.containsMatchIn(command)
}
