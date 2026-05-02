package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.CacheMountAwareRunCommandValidator

object ZypperInstallWithoutCleanValidator : CacheMountAwareRunCommandValidator {
    override val cacheMountTargets = setOf("/var/cache/zypp")
    private val installCommands =
        listOf(
            "install",
            "in",
            "remove",
            "rm",
            "source-install",
            "si",
            "patch",
        )

    private val installCommandsPattern = installCommands.joinToString("|") { Regex.escape(it) }

    val pattern =
        Regex(
            pattern = """^(RUN\s+)?(.*?\bzypper\s+($installCommandsPattern)\b)(?!.*\bzypper\s+(clean|cc)\b.*$).*""",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL),
        )

    override fun isValidWithoutCacheMount(command: String) = !pattern.matches(command)
}
