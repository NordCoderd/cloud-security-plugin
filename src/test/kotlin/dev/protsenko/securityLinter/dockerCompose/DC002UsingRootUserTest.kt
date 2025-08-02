package dev.protsenko.securityLinter.dockerCompose

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerComposeHighlightingBaseTest

class DC002UsingRootUserTest(
    override val ruleFolderName: String = "DC002",
    override val targetInspection: LocalInspectionTool = DockerComposeInspection(),
    override val customFiles: Set<String> = emptySet<String>(),
) : DockerComposeHighlightingBaseTest()
