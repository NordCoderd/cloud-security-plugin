package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.KubernetesHighlightingBaseTest

class KUBE003HostNetworkPidIpc(
    override val ruleFolderName: String = "KUBE003",
    override val targetFileName: String = "pod.yaml",
    override val targetInspection: LocalInspectionTool = HostNetworkPidIpcInspection(),
    override val customFiles: Set<String> = setOf(
        "cronjob.yaml.denied",
        "cronjob-network.yaml.denied",
        "deployment.yaml.denied",
        "deployment-network.yaml.denied",
        "pod.yaml.allowed",
        "pod.yaml.denied",
        "pod-all-acceptable.yaml.allowed",
        "pod-from-rego.yaml.denied",
        "pod-network.yaml.denied"
    )
) : KubernetesHighlightingBaseTest()