package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.KubernetesHighlightingBaseTest

class KUBE012DisallowedVolumeType(
    override val ruleFolderName: String = "KUBE012",
    override val targetFileName: String = "pod.yaml",
    override val targetInspection: LocalInspectionTool = DisallowedVolumeType(),
    override val customFiles: Set<String> =
        setOf(
            "cronjob.yaml.denied",
        ),
) : KubernetesHighlightingBaseTest()
