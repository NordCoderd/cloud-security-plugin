package dev.protsenko.securityLinter.docker.checker.core

interface CacheMountAwareRunCommandValidator : RunCommandValidator {
    val cacheMountTargets: Set<String>

    fun isValidWithoutCacheMount(command: String): Boolean

    override fun isValid(command: String): Boolean {
        if (usesSupportedCacheMount(command) && !removesSupportedCache(command)) {
            return true
        }
        return isValidWithoutCacheMount(command)
    }

    private fun usesSupportedCacheMount(command: String): Boolean =
        MOUNT_FLAG_PATTERN.findAll(command).any { match ->
            val options = parseMountOptions(match.value.removePrefix("--mount="))
            options["type"] == "cache" && normalizeTarget(options["target"]) in normalizedCacheMountTargets()
        }

    private fun removesSupportedCache(command: String): Boolean =
        normalizedCacheMountTargets().any { target ->
            Regex("""rm\s+\S*r\S*\s+.*?${Regex.escape(target)}/\*""").containsMatchIn(command)
        }

    private fun parseMountOptions(mount: String): Map<String, String> =
        mount
            .split(",")
            .mapNotNull { option ->
                val parts = option.split("=", limit = 2)
                if (parts.size == 2) {
                    parts[0].trim() to parts[1].trim()
                } else {
                    null
                }
            }.toMap()

    private fun normalizeTarget(target: String?): String? =
        target
            ?.trim()
            ?.trim('"', '\'')
            ?.trimEnd('/')

    private fun normalizedCacheMountTargets(): Set<String> = cacheMountTargets.mapTo(mutableSetOf()) { it.trimEnd('/') }

    private companion object {
        private val MOUNT_FLAG_PATTERN = Regex("""--mount=\S+""")
    }
}
