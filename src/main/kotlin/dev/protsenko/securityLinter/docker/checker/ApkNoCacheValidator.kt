package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.RunCommandValidator

object ApkNoCacheValidator : RunCommandValidator {
    /**
     * Matches valid Dockerfile RUN commands for Alpine package installation.
     *
     * Two alternative valid forms are accepted:
     *
     * 1. BuildKit cache-mount path: the command has a --mount=type=cache flag whose target
     *    key equals /var/cache/apk (optional trailing slash or surrounding quotes). BuildKit
     *    keeps /var/cache/apk off the final image layer, so --no-cache is not needed.
     *    The exemption is revoked when the command also contains rm -rf /var/cache/apk/star,
     *    which would defeat the cache-mount benefit.
     *
     * 2. Standard path: the command either omits apk add entirely or includes --no-cache;
     *    and it never calls apk update (which bloats image layers); and it never manually
     *    removes /var/cache/apk/star (redundant when --no-cache is already present).
     */
    private val VALID_COMMAND_PATTERN =
        Regex(
            "^\\s*RUN(?:" +
                "(?=.*--mount=type=cache(?=\\S*,target=\"?/var/cache/apk/?\"?)\\S*)(?!.*rm\\s+\\S*r\\S*\\s+.*?/var/cache/apk/\\*)" +
                "|" +
                "(?:(?!.*apk add)|(?=.*--no-cache))(?!.*apk update)(?!.*rm\\s+.*?/var/cache/apk/\\*)" +
                ").*\$",
            RegexOption.DOT_MATCHES_ALL,
        )

    override fun isValid(command: String): Boolean = VALID_COMMAND_PATTERN.matches(command)
}
