package dev.protsenko.securityLinter.utils

import dev.protsenko.securityLinter.docker.checker.ApkNoCacheValidator
import junit.framework.TestCase

class ApkNoCacheApkNoCacheValidatorTest : TestCase() {
    fun testValidCommands() {
        assertTrue(ApkNoCacheValidator.isValid("RUN echo 'Hello World'"))
        assertTrue(ApkNoCacheValidator.isValid("RUN apk add --no-cache git"))
        assertTrue(ApkNoCacheValidator.isValid("RUN apk --no-cache add git"))
        val multiLineValid =
            """
            RUN apk add --no-cache --virtual .build-deps gcc musl-dev && \
                ./configure && \
                make && make install
            """.trimIndent()
        assertTrue(ApkNoCacheValidator.isValid(multiLineValid))
        assertTrue(ApkNoCacheValidator.isValid("RUN ./autoupdate-script.sh"))
        assertTrue(ApkNoCacheValidator.isValid(" RUN apk add --no-cache git"))
        // BuildKit cache mount cases — no --no-cache required (fixing DFS030 false positive)
        assertTrue(ApkNoCacheValidator.isValid("RUN --mount=type=cache,target=/var/cache/apk apk add curl git"))
        assertTrue(ApkNoCacheValidator.isValid("RUN --mount=type=cache,target=/var/cache/apk/ apk add curl"))
        assertTrue(ApkNoCacheValidator.isValid("RUN --mount=type=cache,target=\"/var/cache/apk\" apk add curl"))
        assertTrue(ApkNoCacheValidator.isValid("RUN --mount=type=cache,target=/var/cache/apk,sharing=locked,id=apk apk add curl"))
        assertTrue(ApkNoCacheValidator.isValid("RUN --mount=type=cache,id=apk,target=/var/cache/apk apk add curl"))
        assertTrue(ApkNoCacheValidator.isValid("RUN --mount=type=secret,id=x --mount=type=cache,target=/var/cache/apk apk add curl"))
        val multiLineMount =
            """
            RUN --mount=type=cache,target=/var/cache/apk \
                apk add curl git
            """.trimIndent()
        assertTrue(ApkNoCacheValidator.isValid(multiLineMount))
        // Both mount and --no-cache present — must remain valid without double-detection
        assertTrue(ApkNoCacheValidator.isValid("RUN --mount=type=cache,target=/var/cache/apk apk add --no-cache curl"))
        // Mount covers apk update too
        assertTrue(ApkNoCacheValidator.isValid("RUN --mount=type=cache,target=/var/cache/apk apk update && apk add curl"))
    }

    fun testInvalidCommands() {
        assertFalse(ApkNoCacheValidator.isValid("RUN apk add git"))
        assertFalse(ApkNoCacheValidator.isValid("RUN apk update && apk add git"))
        assertFalse(ApkNoCacheValidator.isValid("RUN apk update"))
        assertFalse(ApkNoCacheValidator.isValid("RUN apk add git && rm -rf /var/cache/apk/*"))
        assertFalse(ApkNoCacheValidator.isValid("RUN apk add git && rm   -rf   /var/cache/apk/*"))
        val multiLineInvalid =
            """
            RUN apk add gcc musl-dev && \
                ./configure
            """.trimIndent()
        assertFalse(ApkNoCacheValidator.isValid(multiLineInvalid))
        assertFalse(ApkNoCacheValidator.isValid("apk add --no-cache git"))
        assertFalse(ApkNoCacheValidator.isValid(""))
        // Mount present but wrong target — still invalid
        assertFalse(ApkNoCacheValidator.isValid("RUN --mount=type=cache,target=/root/.cache apk add curl"))
        // Wrong mount type — only type=cache exempts
        assertFalse(ApkNoCacheValidator.isValid("RUN --mount=type=bind,target=/var/cache/apk apk add curl"))
        assertFalse(ApkNoCacheValidator.isValid("RUN --mount=type=tmpfs,target=/var/cache/apk apk add curl"))
        // Cache mount + explicit rm defeats caching — still invalid
        assertFalse(ApkNoCacheValidator.isValid("RUN --mount=type=cache,target=/var/cache/apk apk add curl && rm -rf /var/cache/apk/*"))
    }
}
