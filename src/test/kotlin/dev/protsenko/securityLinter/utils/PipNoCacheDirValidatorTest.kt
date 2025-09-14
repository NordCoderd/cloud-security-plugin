package dev.protsenko.securityLinter.utils

import dev.protsenko.securityLinter.docker.checker.PipNoCacheDirValidator
import junit.framework.TestCase

class PipNoCacheDirValidatorTest : TestCase() {
    fun testValidCommands() {
        // Commands with --no-cache-dir (secure)
        assertTrue(PipNoCacheDirValidator.isValid("RUN pip install --no-cache-dir package"))
        assertTrue(PipNoCacheDirValidator.isValid("RUN pip install package --no-cache-dir"))
        assertTrue(PipNoCacheDirValidator.isValid("RUN pip install --no-cache-dir package==1.2.3"))
        assertTrue(PipNoCacheDirValidator.isValid("RUN pip install --upgrade --no-cache-dir package"))
        assertTrue(PipNoCacheDirValidator.isValid("RUN pip install --no-cache-dir package && echo 'done'"))

        // Non-pip install commands (not applicable)
        assertTrue(PipNoCacheDirValidator.isValid("RUN apt-get update"))
        assertTrue(PipNoCacheDirValidator.isValid("RUN echo 'hello world'"))
        assertTrue(PipNoCacheDirValidator.isValid("RUN npm install package"))
    }

    fun testInvalidCommands() {
        // pip install without --no-cache-dir (security violations)
        assertFalse(PipNoCacheDirValidator.isValid("RUN pip install package"))
        assertFalse(PipNoCacheDirValidator.isValid("RUN pip install package==1.2.3"))
        assertFalse(PipNoCacheDirValidator.isValid("RUN pip install --upgrade package"))
        assertFalse(PipNoCacheDirValidator.isValid("RUN pip install package && echo 'done'"))
        assertFalse(PipNoCacheDirValidator.isValid("RUN pip install -r requirements.txt"))

        // Multi-line commands
        assertFalse(PipNoCacheDirValidator.isValid("RUN pip install \\\n    package \\\n    another-package"))
    }

    fun testEdgeCases() {
        // Commands that don't start with RUN
        assertTrue(PipNoCacheDirValidator.isValid("pip install package"))
        assertTrue(PipNoCacheDirValidator.isValid("COPY . ."))

        // Empty or malformed commands
        assertTrue(PipNoCacheDirValidator.isValid(""))
        assertTrue(PipNoCacheDirValidator.isValid("RUN"))
        assertTrue(PipNoCacheDirValidator.isValid("RUN "))
    }
}
