package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.kubernetes.quickfix.ReplaceValueToFalseQuickFix
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.ALLOW_PRIVILEGE_ESCALATION
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.PRIVILEGED
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.containerTypes
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.evaluateSpecPrefix
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.supportedKinds
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.YAMLUtil
import org.jetbrains.yaml.psi.*

class PrivilegedContainersInspection : LocalInspectionTool() {

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
                            YamlPath.findByYamlPath("${specPrefix}$containerType", document) as? YAMLSequence
                                ?: continue

                        for (containerItem in containers.items) {
                            val containerYaml = containerItem.value as? YAMLMapping ?: continue

                            val isPrivilegedElement =
                                YamlPath.findByYamlPath(PRIVILEGED, containerYaml)

                            val isAllowPrivilegeEscalationElement =
                                YamlPath.findByYamlPath(ALLOW_PRIVILEGE_ESCALATION, containerYaml)

                            val elementsToValidate = listOfNotNull(isPrivilegedElement, isAllowPrivilegeEscalationElement)

                            for (elementToValidate in elementsToValidate) {
                                if (elementToValidate !is YAMLScalar) continue
                                val booleanValue = elementToValidate.textValue.toBooleanStrictOrNull()

                                if (booleanValue != false){
                                    val descriptor = HtmlProblemDescriptor(
                                        elementToValidate,
                                        SecurityPluginBundle.message("kube004.documentation"),
                                        SecurityPluginBundle.message("kube004.using-privileged-containers"),
                                        ProblemHighlightType.ERROR, arrayOf(
                                            ReplaceValueToFalseQuickFix(
                                                SecurityPluginBundle.message("kube004.qf.fix-value")
                                            )
                                        )
                                    )
                                    holder.registerProblem(descriptor)
                                }
                            }
                        }
                    }

                }

                super.visitFile(file)
            }
        }
    }
}