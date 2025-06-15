package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.containerTypes
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.evaluateSpecPrefix
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.insecureCapabilities
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.supportedKinds
import dev.protsenko.securityLinter.kubernetes.utils.unquoted
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.YAMLUtil
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLQuotedText
import org.jetbrains.yaml.psi.YAMLSequence

class InsecureCapabilitiesInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                if (file !is YAMLFile) return

                val documents = file.documents

                for (document in documents) {
                    val kind = YAMLUtil.getQualifiedKeyInDocument(document, listOf("kind")) ?: return
                    val kindValue = kind.valueText
                    if (kindValue !in supportedKinds) return

                    val specPrefix = evaluateSpecPrefix(kindValue)

                    for (containerType in containerTypes) {
                        val containers =
                            YamlPath.findByYamlPath("${specPrefix}$containerType", document) as? YAMLSequence ?: continue

                        for (containerItem in containers.items) {
                            val containerYaml = containerItem.value as? YAMLMapping ?: continue

                            val capabilities = (YamlPath.findByYamlPath(
                                "securityContext.capabilities.add",
                                containerYaml
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

                            print(containerYaml)
                        }
                    }
                }

                super.visitFile(file)
            }
        }
    }
}

