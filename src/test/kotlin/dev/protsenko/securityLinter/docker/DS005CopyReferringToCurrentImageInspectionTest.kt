package dev.protsenko.securityLinter.docker

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerHighlightingBaseTest
import dev.protsenko.securityLinter.docker.inspection.copyAndAdd.DockerfileCopyAndAddInspection

class DS005CopyReferringToCurrentImageInspectionTest(
    override val ruleFolderName: String = "DS005",
    override val customFiles: Set<String> = setOf("Dockerfile-multi-step.denied"),
    override val targetInspection: LocalInspectionTool = DockerfileCopyAndAddInspection(),
) : DockerHighlightingBaseTest()
