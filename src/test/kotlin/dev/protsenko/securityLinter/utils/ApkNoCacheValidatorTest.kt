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
    }
}
