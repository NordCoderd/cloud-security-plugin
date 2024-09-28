package dev.protsenko.securityLinter.utils

import dev.protsenko.securityLinter.docker.checker.YumInstallWithoutCleanValidator
import junit.framework.TestCase

class YumInstallCheckerTest : TestCase() {

    fun testInvalidCommands() {
        val commands = listOf(
            "RUN yum install -y package",
            "RUN yum install package && some_other_command",
            "RUN echo 'Start' && yum install package",
            "RUN yum install -y package; other_command",
            "RUN \\\n yum install package",
            "RUN YUM INSTALL Package",
            "RUN yum    install    package",
            "RUN yum install package || echo 'Failed to install'",
            "RUN yum install package | bash",
            "RUN yum update && yum install package",
            "RUN yum clean all && yum install package"
        )
        for (command in commands) {
            assertFalse(
                "Command '$command' should be recognized as containing 'yum install' without 'yum clean all'",
                YumInstallWithoutCleanValidator.isValid(command)
            )
        }
    }

    fun testValidCommands() {
        val commands = listOf(
            "RUN yum install -y package && yum clean all",
            "RUN yum install package && yum clean all && another_command",
            "RUN echo 'No install here'",
            "RUN yum INSTALL package && YUM CLEAN ALL",
            "RUN yum install package && some_command && yum clean all",
            "RUN yum install package; yum clean all",
            "RUN yum clean all"
        )
        for (command in commands) {
            assertTrue(
                "Command '$command' should not be recognized as containing 'yum install' without 'yum clean all'",
                YumInstallWithoutCleanValidator.isValid(command)
            )
        }
    }
}

