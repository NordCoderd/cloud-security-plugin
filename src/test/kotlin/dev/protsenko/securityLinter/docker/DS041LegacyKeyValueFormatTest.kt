package dev.protsenko.securityLinter.docker

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.DockerHighlightingBaseTest
import dev.protsenko.securityLinter.docker.inspection.keyValue.LegacyKeyValueFormat

class DS041LegacyKeyValueFormatTest(
    override val ruleFolderName: String = "DS041",
    override val customFiles: Set<String> = setOf(
        "Dockerfile-multiline-env.denied",
        "Dockerfile-multiline-env.allowed",
    ),
    override val targetInspection: LocalInspectionTool = LegacyKeyValueFormat(),
) : DockerHighlightingBaseTest()
