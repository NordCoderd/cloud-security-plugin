package dev.protsenko.securityLinter.docker_compose

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerComposeHighlightingBaseTest

class DC001InvalidImageDefinitionTest(
    override val ruleFolderName: String = "DC001",
    override val targetInspection: LocalInspectionTool = DockerComposeInspection(),
    override val customFiles: Set<String> = emptySet()
): DockerComposeHighlightingBaseTest()