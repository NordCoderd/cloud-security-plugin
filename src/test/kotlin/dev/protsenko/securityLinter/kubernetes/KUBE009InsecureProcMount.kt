package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.KubernetesHighlightingBaseTest

class KUBE009InsecureProcMount(
    override val ruleFolderName: String = "KUBE009",
    override val targetFileName: String = "pod.yaml",
    override val targetInspection: LocalInspectionTool = InsecureProcMount(),
    override val customFiles: Set<String> = setOf()
) : KubernetesHighlightingBaseTest()