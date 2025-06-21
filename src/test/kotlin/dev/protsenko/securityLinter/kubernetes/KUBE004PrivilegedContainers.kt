package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.KubernetesHighlightingBaseTest

class KUBE004PrivilegedContainers(
    override val ruleFolderName: String = "KUBE004",
    override val targetFileName: String = "pod.yaml",
    override val targetInspection: LocalInspectionTool = PrivilegedContainersInspection(),
    override val customFiles: Set<String> = setOf(
        "pod-null.yaml.allowed",
        "deployment.yaml.denied"
    )
) : KubernetesHighlightingBaseTest()