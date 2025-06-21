package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import dev.protsenko.securityLinter.core.KubernetesHighlightingBaseTest

class KUBE005HostPathVolumes(
    override val ruleFolderName: String = "KUBE005",
    override val targetFileName: String = "pod.yaml",
    override val targetInspection: LocalInspectionTool = HostPathVolumesInspection(),
    override val customFiles: Set<String> = setOf(
        "cronjob.yaml.denied"
    )
) : KubernetesHighlightingBaseTest()