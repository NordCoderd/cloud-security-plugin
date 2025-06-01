package dev.protsenko.securityLinter.docker

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerHighlightingBaseTest
import dev.protsenko.securityLinter.docker.inspection.run.DockerfileRunInspection

class DS034ConsecutiveRunCommands(
    override val ruleFolderName: String = "DS034",
    override val customFiles: Set<String> = setOf(
        "Dockerfile-three-commands.denied",
        "Dockerfile-user-command-between.allowed",
        "Dockerfile-two-and-one-command.denied"
    ),
    override val targetInspection: LocalInspectionTool = DockerfileRunInspection()
) : DockerHighlightingBaseTest()