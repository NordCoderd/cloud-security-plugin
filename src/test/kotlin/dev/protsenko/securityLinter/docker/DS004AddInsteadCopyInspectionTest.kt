package dev.protsenko.securityLinter.docker

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerHighlightingBaseTest
import dev.protsenko.securityLinter.docker.inspection.copyAndAdd.DockerfileCopyAndAddInspection

class DS004AddInsteadCopyInspectionTest(
    override val ruleFolderName: String = "DS004",
    override val customFiles: Set<String> =
        setOf<String>(
            "Dockerfile-brackets.denied",
            "Dockerfile-empty-add.allowed",
        ),
    override val targetInspection: LocalInspectionTool = DockerfileCopyAndAddInspection(),
) : DockerHighlightingBaseTest()
