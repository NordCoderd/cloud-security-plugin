package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.KubernetesHighlightingBaseTest

class KUBE007AppArmorOverride(
    override val ruleFolderName: String = "KUBE007",
    override val targetFileName: String = "pod.yaml",
    override val targetInspection: LocalInspectionTool = AppArmorOverrideInspection(),
    override val customFiles: Set<String> = setOf()
) : KubernetesHighlightingBaseTest()