package dev.protsenko.securityLinter.docker.checker

import dev.protsenko.securityLinter.docker.checker.core.CacheMountAwareRunCommandValidator

object PipNoCacheDirValidator : CacheMountAwareRunCommandValidator {
    override val cacheMountTargets = setOf("/root/.cache/pip")

    /**
     * Regular expression to detect pip install commands that lack the --no-cache-dir option.
     * BuildKit cache-mount exemption is handled by CacheMountAwareRunCommandValidator.
     */
    private val regex =
        Regex(
            "^RUN\\s+(?=.*pip\\s+install)(?!.*--no-cache-dir).*$",
            RegexOption.DOT_MATCHES_ALL,
        )

    /**
     * Validates if a Docker RUN command follows security best practices for pip install
     * when no supported BuildKit cache mount is present.
     *
     * @param command The Docker command string to validate
     * @return false if pip install is used without --no-cache-dir, true otherwise
     */
    override fun isValidWithoutCacheMount(command: String): Boolean = !regex.matches(command)
}
