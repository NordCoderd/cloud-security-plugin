package dev.protsenko.securityLinter.utils

import dev.protsenko.securityLinter.docker.checker.core.CacheMountAwareRunCommandValidator
import junit.framework.TestCase

class CacheMountAwareRunCommandValidatorTest : TestCase() {
    fun testValidCacheMountsSkipWrappedValidation() {
        val commands =
            listOf(
                "RUN --mount=type=cache,target=/var/cache/example package-manager install curl",
                "RUN --mount=type=cache,target=/var/cache/example/ package-manager install curl",
                "RUN --mount=type=cache,target=\"/var/cache/example\" package-manager install curl",
                "RUN --mount=type=cache,target='/var/cache/example' package-manager install curl",
                "RUN --mount=type=cache,id=example,target=/var/cache/example package-manager install curl",
                "RUN --mount=type=secret,id=x --mount=type=cache,target=/var/cache/example package-manager install curl",
                """
                RUN --mount=type=cache,target=/var/cache/example \
                    package-manager install curl
                """.trimIndent(),
            )

        for (command in commands) {
            assertTrue("Command '$command' should be valid", AlwaysInvalidCacheMountAwareValidator.isValid(command))
        }
    }

    fun testUnsupportedMountsUseWrappedValidation() {
        val commands =
            listOf(
                "RUN --mount=type=cache,target=/var/cache/other package-manager install curl",
                "RUN --mount=type=bind,target=/var/cache/example package-manager install curl",
                "RUN --mount=type=tmpfs,target=/var/cache/example package-manager install curl",
                "RUN package-manager install curl",
            )

        for (command in commands) {
            assertFalse("Command '$command' should be invalid", AlwaysInvalidCacheMountAwareValidator.isValid(command))
        }
    }

    fun testCacheRemovalInvalidatesSupportedMount() {
        assertFalse(
            AlwaysInvalidCacheMountAwareValidator.isValid(
                "RUN --mount=type=cache,target=/var/cache/example package-manager install curl && rm -rf /var/cache/example/*",
            ),
        )
        assertFalse(
            AlwaysInvalidCacheMountAwareValidator.isValid(
                "RUN --mount=type=cache,target=/var/cache/example package-manager install curl && rm   -rf   /var/cache/example/*",
            ),
        )
    }

    fun testWrappedValidationStillAllowsCommandsWithoutSupportedMount() {
        assertTrue(KeywordValidCacheMountAwareValidator.isValid("RUN safe-command"))
        assertFalse(KeywordValidCacheMountAwareValidator.isValid("RUN unsafe-command"))
    }

    private object AlwaysInvalidCacheMountAwareValidator : CacheMountAwareRunCommandValidator {
        override val cacheMountTargets = setOf("/var/cache/example")

        override fun isValidWithoutCacheMount(command: String): Boolean = false
    }

    private object KeywordValidCacheMountAwareValidator : CacheMountAwareRunCommandValidator {
        override val cacheMountTargets = setOf("/var/cache/example")

        override fun isValidWithoutCacheMount(command: String): Boolean = command == "RUN safe-command"
    }
}
