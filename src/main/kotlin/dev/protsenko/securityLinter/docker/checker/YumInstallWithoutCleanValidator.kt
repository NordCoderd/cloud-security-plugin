package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.CacheMountAwareRunCommandValidator

object YumInstallWithoutCleanValidator : CacheMountAwareRunCommandValidator {
    override val cacheMountTargets = setOf("/var/cache/yum")

    private val yumInstallWithoutCleanRegex =
        Regex(
            pattern = "(?i)\\byum\\s+install\\b(?:(?!\\byum\\s+clean\\s+all\\b).)*$",
            options = setOf(RegexOption.DOT_MATCHES_ALL),
        )

    override fun isValidWithoutCacheMount(command: String) = !yumInstallWithoutCleanRegex.containsMatchIn(command)
}
