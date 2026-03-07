package dev.protsenko.securityLinter.docker

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerHighlightingBaseTest
import dev.protsenko.securityLinter.docker.inspection.copyAndAdd.DockerfileCopyAndAddInspection

class DS038CopyRelativeDestinationWithoutWorkdirInspectionTest(
    override val ruleFolderName: String = "DS038",
    override val customFiles: Set<String> =
        setOf<String>(
            "Dockerfile-json.denied",
            "Dockerfile-multi-stage.denied",
            "Dockerfile-absolute-path.allowed",
        ),
    override val targetInspection: LocalInspectionTool = DockerfileCopyAndAddInspection(),
) : DockerHighlightingBaseTest()
