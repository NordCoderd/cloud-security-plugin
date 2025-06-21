package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.KubernetesHighlightingBaseTest

class KUBE001NonRootContainers(
    override val ruleFolderName: String = "KUBE001",
    override val targetFileName: String = "pod.yaml",
    override val targetInspection: LocalInspectionTool = NonRootContainerInspection(),
    override val customFiles: Set<String> = setOf(
        "pod-global-true-container-false.yaml.denied",
        // "pod-global-null.yaml.denied",
        "pod-global-null-container-true.allowed",
        "pod-container-user-zero.yaml.denied",
        "pod-global-user-zero.yaml.denied",
        "pod-container-group-zero.yaml.denied",
        "pod-global-group-zero.yaml.denied",
        "deployment.yaml.allowed",
        "deployment.yaml.denied",
        "cronjob.yaml.denied",
        "cronjob-run-as-user.yaml.denied",
        // "pod-global-null-security-context.yaml.denied"
    )
): KubernetesHighlightingBaseTest()