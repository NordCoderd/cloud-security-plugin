package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.KubernetesHighlightingBaseTest

class KUBE002InsecureCapabilities(
    override val ruleFolderName: String = "KUBE002",
    override val targetFileName: String = "pod.yaml",
    override val targetInspection: LocalInspectionTool = InsecureCapabilitiesInspection(),
    override val customFiles: Set<String> = setOf(
        "cronjob.yaml.denied",
        "deployment.yaml.denied"
    )
): KubernetesHighlightingBaseTest()