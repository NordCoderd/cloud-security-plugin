package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.KubernetesHighlightingBaseTest

class KUBE011InsecureSysctls(
    override val ruleFolderName: String = "KUBE011",
    override val targetFileName: String = "pod.yaml",
    override val targetInspection: LocalInspectionTool = InsecureSysctlsInspection(),
    override val customFiles: Set<String> = setOf()
) : KubernetesHighlightingBaseTest()