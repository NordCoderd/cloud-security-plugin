package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.KubernetesHighlightingBaseTest

class KUBE006HostPorts(
    override val ruleFolderName: String = "KUBE006",
    override val targetFileName: String = "pod.yaml",
    override val targetInspection: LocalInspectionTool = HostPortsInspection(),
    override val customFiles: Set<String> = setOf(
        "cronjob.yaml.denied"
    )
) : KubernetesHighlightingBaseTest()