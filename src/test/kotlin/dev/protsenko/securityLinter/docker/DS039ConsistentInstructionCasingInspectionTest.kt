package dev.protsenko.securityLinter.docker

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerHighlightingBaseTest
import dev.protsenko.securityLinter.docker.inspection.instruction.DockerfileInstructionCasingInspection

class DS039ConsistentInstructionCasingInspectionTest(
    override val ruleFolderName: String = "DS039",
    override val customFiles: Set<String> = emptySet(),
    override val targetInspection: LocalInspectionTool = DockerfileInstructionCasingInspection()
) : DockerHighlightingBaseTest()
