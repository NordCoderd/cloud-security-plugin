package dev.protsenko.securityLinter.utils

import dev.protsenko.securityLinter.docker.checker.CurlBashingValidator
import junit.framework.TestCase

class CurlBashingCheckerTest : TestCase() {
    fun testInvalidCommands() {
        val commands =
            listOf(
                "RUN curl -s https://example.com | bash",
                "RUN wget -q https://example.com \n| bash",
                "RUN curl -o- https://example.com \\n> bash",
                "RUN wget https://example.com \\\n| sh",
            )
        for (command in commands) {
            assertFalse(
                "Command '$command' should be invalid for curl bashing",
                CurlBashingValidator.isValid(command),
            )
        }
    }

    fun testValidCommands() {
        val commands =
            listOf(
                "RUN echo Hello World",
                "RUN curl -s https://example.com",
                "RUN wget -q https://example.com",
                "RUN echo 'Some command' | bash",
                """
                RUN curl -L "https://github.com/go-task/task/releases/download/${'$'}{TASK_VERSION}/task_${'$'}{TARGETOS}_${'$'}{TARGETARCH}.tar.gz" -o "/tmp/task_${'$'}{TARGETOS}_${'$'}{TARGETARCH}.tar.gz" \
                    && curl -L "https://github.com/go-task/task/releases/download/${'$'}{TASK_VERSION}/task_checksums.txt" -o "/tmp/task_checksums.txt" \
                    && cd /tmp && grep "task_${'$'}{TARGETOS}_${'$'}{TARGETARCH}.tar.gz" task_checksums.txt | sha256sum -c - \
                    && tar -C /opt -xzf "/tmp/task_${'$'}{TARGETOS}_${'$'}{TARGETARCH}.tar.gz" task
                """.trimIndent(),
                "RUN curl -s https://example.com > /dev/null",
                "RUN wget -O /tmp/file https://example.com",
                "RUN curl -s https://example.com | grep foo",
            )
        for (command in commands) {
            assertTrue(
                "Command '$command' should be valid for curl bashing",
                CurlBashingValidator.isValid(command),
            )
        }
    }
}
