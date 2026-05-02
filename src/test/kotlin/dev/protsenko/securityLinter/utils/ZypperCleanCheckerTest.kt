package dev.protsenko.securityLinter.utils

import dev.protsenko.securityLinter.docker.checker.ZypperInstallWithoutCleanValidator
import junit.framework.TestCase

class ZypperCleanCheckerTest : TestCase() {

    fun testValidCommands() {
        val commands = listOf(
            "RUN zypper install package && zypper clean",
            "RUN zypper in package && zypper cc",
            "RUN zypper remove package && zypper clean",
            "RUN zypper remove package && \\\n zypper clean",
            "RUN zypper rm package && zypper cc",
            "RUN zypper patch && zypper clean",
            "RUN echo 'No zypper commands here'",
            "RUN zypper install package && other_command && zypper clean",
            "RUN zypper install package; zypper clean",
            "RUN zypper install package && zypper cc && echo 'Done'",
            "RUN --mount=type=cache,target=/var/cache/zypp zypper install curl",
            "RUN --mount=type=cache,target=/var/cache/zypp/ zypper install curl",
            "RUN --mount=type=cache,target=\"/var/cache/zypp\" zypper install curl",
            "RUN --mount=type=cache,id=zypp,target=/var/cache/zypp zypper install curl",
            "RUN --mount=type=secret,id=x --mount=type=cache,target=/var/cache/zypp zypper install curl",
        )
        for (command in commands) {
            assertTrue(
                "Command '$command' should be valid",
                ZypperInstallWithoutCleanValidator.isValid(command)
            )
        }
    }

    fun testInvalidCommands() {
        val commands = listOf(
            "RUN zypper install package",
            "RUN zypper in package",
            "RUN zypper remove package",
            "RUN zypper install package && echo 'No clean command'",
            "RUN zypper install package && zypper update",
            //FIXME: Corner case
            // "RUN zypper install package && zypper clean; zypper install another_package",
            "RUN zypper clean && zypper install package",
            "RUN zypper install package && other_command",
            "RUN zypper in package && zypper refresh",
            "RUN --mount=type=cache,target=/root/.cache zypper install curl",
            "RUN --mount=type=bind,target=/var/cache/zypp zypper install curl",
            "RUN --mount=type=tmpfs,target=/var/cache/zypp zypper install curl",
            "RUN --mount=type=cache,target=/var/cache/zypp zypper install curl && rm -rf /var/cache/zypp/*",
        )
        for (command in commands) {
            assertFalse(
                "Command '$command' should be invalid",
                ZypperInstallWithoutCleanValidator.isValid(command)
            )
        }
    }
}
