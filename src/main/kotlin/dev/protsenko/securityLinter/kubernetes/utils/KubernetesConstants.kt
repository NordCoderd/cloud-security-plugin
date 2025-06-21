package dev.protsenko.securityLinter.kubernetes.utils

object KubernetesConstants {

    val specInTemplateKindTypes = setOf("Deployment", "ReplicaSet", "DaemonSet", "StatefulSet", "Job")
    val supportedKinds = specInTemplateKindTypes + setOf("Pod", "CronJob")

    val containerTypes = listOf("spec.containers", "spec.initContainers", "spec.ephemeralContainers")

    const val SPEC = "spec"
    const val SECURITY_CONTEXT = "spec.securityContext"
    const val RUN_AS_NON_ROOT = "securityContext.runAsNonRoot"
    const val RUN_AS_USER = "securityContext.runAsUser"
    const val RUN_AS_GROUP = "securityContext.runAsGroup"
    const val PRIVILEGED = "securityContext.privileged"
    const val ALLOW_PRIVILEGE_ESCALATION = "securityContext.allowPrivilegeEscalation"

    fun evaluateSpecPrefix(kind: String): String {
        if (kind == "Pod") return ""
        if (kind == "CronJob") return "spec.jobTemplate.spec.template."
        if (kind in specInTemplateKindTypes) return "spec.template."
        return ""
    }

    val insecureCapabilities = setOf(
        "SETPCAP",
        "NET_ADMIN",
        "NET_RAW",
        "SYS_MODULE",
        "SYS_RAWIO",
        "SYS_PTRACE",
        "SYS_ADMIN",
        "SYS_BOOT",
        "MAC_OVERRIDE",
        "MAC_ADMIN",
        "PERFMON",
        "ALL",
        "BPF"
    )
}
