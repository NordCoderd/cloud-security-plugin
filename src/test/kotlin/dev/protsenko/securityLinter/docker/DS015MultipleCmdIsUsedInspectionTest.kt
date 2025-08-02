package dev.protsenko.securityLinter.docker

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerHighlightingBaseTest
import dev.protsenko.securityLinter.docker.inspection.cmdAndEntrypoint.DockerfileCmdAndEntrypointInspection

class DS015MultipleCmdIsUsedInspectionTest(
    override val ruleFolderName: String = "DS015",
    override val customFiles: Set<String> = setOf("Dockerfile-multiple.denied"),
    override val targetInspection: LocalInspectionTool = DockerfileCmdAndEntrypointInspection(),
) : DockerHighlightingBaseTest()
