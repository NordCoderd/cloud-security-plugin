package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.CacheMountAwareRunCommandValidator

object ApkNoCacheValidator : CacheMountAwareRunCommandValidator {
    override val cacheMountTargets = setOf("/var/cache/apk")

    /**
     * Matches valid Dockerfile RUN commands for Alpine package installation.
     *
     * Standard path: the command either omits apk add entirely or includes --no-cache;
     * and it never calls apk update; and it never manually removes /var/cache/apk/star.
     *
     * BuildKit cache-mount exemption is handled by CacheMountAwareRunCommandValidator.
     */
    private val validCommandPattern =
        Regex(
            "^\\s*RUN(?:(?!.*apk add)|(?=.*--no-cache))(?!.*apk update)(?!.*rm\\s+.*?/var/cache/apk/\\*).*$",
            RegexOption.DOT_MATCHES_ALL,
        )

    override fun isValidWithoutCacheMount(command: String): Boolean = validCommandPattern.matches(command)
}
