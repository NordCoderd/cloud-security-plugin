package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLSequence

class HostPortsInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor =
        object : BaseKubernetesVisitor() {
            override fun analyze(
                specPrefix: String,
                document: YAMLDocument,
            ) {
                val containers = containers(specPrefix, document)
                for (container in containers) {
                    val ports =
                        (
                            YamlPath.findByYamlPath(
                                "ports",
                                container,
                            ) as? YAMLSequence
                        ) ?: continue

                    for (port in ports.items) {
                        val portMapping = port.value as? YAMLMapping ?: continue
                        val hostPort = portMapping.getKeyValueByKey("hostPort") ?: continue
                        val portValue = hostPort.valueText
                        // FTI: allow list and removing whole yaml node
                        if (portValue != "0") {
                            val descriptor =
                                HtmlProblemDescriptor(
                                    port,
                                    SecurityPluginBundle.message("kube006.documentation"),
                                    SecurityPluginBundle.message("kube006.problem-text"),
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
