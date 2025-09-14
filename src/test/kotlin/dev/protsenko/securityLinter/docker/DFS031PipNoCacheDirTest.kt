package dev.protsenko.securityLinter.docker

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerHighlightingBaseTest
import dev.protsenko.securityLinter.docker.inspection.run.DockerfileRunInspection

class DFS031PipNoCacheDirTest(
    override val ruleFolderName: String = "DFS031",
    override val customFiles: Set<String> = emptySet(),
    override val targetInspection: LocalInspectionTool = DockerfileRunInspection(),
) : DockerHighlightingBaseTest()
