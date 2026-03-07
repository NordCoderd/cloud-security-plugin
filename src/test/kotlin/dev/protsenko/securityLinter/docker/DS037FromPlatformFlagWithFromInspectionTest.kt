package dev.protsenko.securityLinter.docker

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerHighlightingBaseTest
import dev.protsenko.securityLinter.docker.inspection.from.DockerfileFromInspection

class DS037FromPlatformFlagWithFromInspectionTest(
    override val ruleFolderName: String = "DS037",
    override val customFiles: Set<String> = setOf(
        "Dockerfile-uppercase.denied",
        "Dockerfile-variable-platform.denied",
    ),
    override val targetInspection: LocalInspectionTool = DockerfileFromInspection()
) : DockerHighlightingBaseTest()
