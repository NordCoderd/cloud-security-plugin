package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLSequence

class InsecureSysctlsInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor {
        return object : BaseKubernetesVisitor() {
            override fun analyze(
                specPrefix: String,
                document: YAMLDocument,
            ) {
                val seccompProfileType =
                    YamlPath.findByYamlPath(
                        "$specPrefix$SYSCTL_PATH",
                        document,
                    ) as? YAMLSequence ?: return

                for (sysctl in seccompProfileType.items) {
                    val sysctlKey =
                        sysctl
                            .keysValues
                            .firstOrNull { it.name == "name" } ?: continue
                    val sysctlValueText = sysctlKey.valueText
                    if (sysctlValueText !in allowedSysctls) {
                        val descriptor =
                            HtmlProblemDescriptor(
                                sysctl,
                                SecurityPluginBundle.message("kube011.documentation"),
                                SecurityPluginBundle.message("kube011.problem-text"),
                                ProblemHighlightType.ERROR,
                                emptyArray(),
                            )
                        holder.registerProblem(descriptor)
                    }
                }
            }
        }
    }
}

private const val SYSCTL_PATH = "spec.securityContext.sysctls"
private val allowedSysctls =
    setOf(
        "",
        "kernel.shm_rmid_forced",
        "net.ipv4.ip_local_port_range",
        "net.ipv4.ip_unprivileged_port_start",
        "net.ipv4.tcp_syncookies",
        "net.ipv4.ping_group_range",
        "net.ipv4.ip_local_reserved_ports",
        "net.ipv4.tcp_keepalive_time",
        "net.ipv4.tcp_fin_timeout",
        "net.ipv4.tcp_keepalive_intvl",
        "net.ipv4.tcp_keepalive_probes",
    )
