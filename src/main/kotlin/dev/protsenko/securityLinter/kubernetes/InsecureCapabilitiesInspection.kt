package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.insecureCapabilities
import dev.protsenko.securityLinter.kubernetes.utils.unquoted
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLQuotedText
import org.jetbrains.yaml.psi.YAMLSequence

class InsecureCapabilitiesInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : BaseKubernetesVisitor() {
            override fun analyze(specPrefix: String, document: YAMLDocument) {
                val containers = containers(specPrefix, document)
                for (container in containers) {
                    val capabilities = (YamlPath.findByYamlPath(
                        "securityContext.capabilities.add",
                        container
                    ) as? YAMLSequence) ?: continue

                    for (capability in capabilities.items) {
                        val capabilityYamlValue = capability.value as? YAMLQuotedText ?: continue
                        val capabilityValue = capabilityYamlValue.unquoted()
                        if (capabilityValue in insecureCapabilities) {

                            val descriptor = HtmlProblemDescriptor(
                                capabilityYamlValue,
                                SecurityPluginBundle.message("kube002.documentation"),
                                SecurityPluginBundle.message("kube002.insecure-capabilities"),
                                ProblemHighlightType.ERROR, emptyArray()
                            )

                            holder.registerProblem(descriptor)
                        }
                    }
                }
            }
        }
    }
}

