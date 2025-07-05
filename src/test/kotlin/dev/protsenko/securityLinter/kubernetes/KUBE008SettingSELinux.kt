package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.KubernetesHighlightingBaseTest

class KUBE008SettingSELinux(
    override val ruleFolderName: String = "KUBE008",
    override val targetFileName: String = "pod.yaml",
    override val targetInspection: LocalInspectionTool = SettingSELinuxInspection(),
    override val customFiles: Set<String> = setOf()
) : KubernetesHighlightingBaseTest()