package dev.protsenko.securityLinter.rego

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerComposeHighlightingBaseTest
import dev.protsenko.securityLinter.core.RegoHighlightingBaseTest
import dev.protsenko.securityLinter.docker_compose.DockerComposeInspection

class RB001NonRootContainers(
    override val ruleFolderName: String = "RB001",
    override val targetInspection: LocalInspectionTool = RegoBasedInspection(),
    override val customFiles: Set<String> = emptySet()
): RegoHighlightingBaseTest()