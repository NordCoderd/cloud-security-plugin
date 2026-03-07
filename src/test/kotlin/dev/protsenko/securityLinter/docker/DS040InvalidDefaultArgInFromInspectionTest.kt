package dev.protsenko.securityLinter.docker

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerHighlightingBaseTest
import dev.protsenko.securityLinter.docker.inspection.from.InvalidDefaultArgInFromInspection

class DS040InvalidDefaultArgInFromInspectionTest(
    override val ruleFolderName: String = "DS040",
    override val customFiles: Set<String> = setOf(
        "Dockerfile-default-with-fallback.allowed",
        "Dockerfile-default-can-be-empty.allowed",
        "Dockerfile-digest-with-empty-arg.denied",
        "Dockerfile-global-arg-in-path.denied",
    ),
    override val targetInspection: LocalInspectionTool = InvalidDefaultArgInFromInspection()
) : DockerHighlightingBaseTest()
