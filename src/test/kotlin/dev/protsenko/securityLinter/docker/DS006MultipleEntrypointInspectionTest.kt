package dev.protsenko.securityLinter.docker

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerHighlightingBaseTest
import dev.protsenko.securityLinter.docker.inspection.cmdAndEntrypoint.DockerfileCmdAndEntrypointInspection

class DS006MultipleEntrypointInspectionTest(
    override val ruleFolderName: String = "DS006",
    override val customFiles: Set<String> = emptySet<String>(),
    override val targetInspection: LocalInspectionTool = DockerfileCmdAndEntrypointInspection(),
) : DockerHighlightingBaseTest()
