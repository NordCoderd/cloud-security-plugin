package dev.protsenko.securityLinter.docker

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerHighlightingBaseTest
import dev.protsenko.securityLinter.docker.inspection.copyAndAdd.DockerfileCopyAndAddInspection

class DS010UseSlashForCopyArgsInspectionTest(
    override val ruleFolderName: String = "DS010",
    override val customFiles: Set<String> =
        setOf<String>(
            "Dockerfile-multiply-without-brackets.denied",
            "Dockerfile-multiply-without-brackets.allowed",
            "Dockerfile-copy-with-arguments.allowed",
            "Dockerfile-common.allowed",
            "Dockerfile-json-common.allowed",
        ),
    override val targetInspection: LocalInspectionTool = DockerfileCopyAndAddInspection(),
) : DockerHighlightingBaseTest()
