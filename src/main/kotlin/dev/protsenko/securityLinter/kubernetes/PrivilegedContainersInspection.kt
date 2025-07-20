package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.kubernetes.quickfix.ReplaceValueToFalseQuickFix
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.ALLOW_PRIVILEGE_ESCALATION
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.PRIVILEGED
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.psi.*

class PrivilegedContainersInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : BaseKubernetesVisitor() {
            override fun analyze(specPrefix: String, document: YAMLDocument) {
                val containers = containers(specPrefix, document)
                for (containerItem in containers) {
                    val isPrivilegedElement =
                        YamlPath.findByYamlPath(PRIVILEGED, containerItem)

                    val isAllowPrivilegeEscalationElement =
                        YamlPath.findByYamlPath(ALLOW_PRIVILEGE_ESCALATION, containerItem)

                    val elementsToValidate = listOfNotNull(isPrivilegedElement, isAllowPrivilegeEscalationElement)

                    for (elementToValidate in elementsToValidate) {
                        if (elementToValidate !is YAMLScalar) continue
                        val booleanValue = elementToValidate.textValue.toBooleanStrictOrNull()

                        if (booleanValue != false){
                            val descriptor = HtmlProblemDescriptor(
                                elementToValidate,
                                SecurityPluginBundle.message("kube004.documentation"),
                                SecurityPluginBundle.message("kube004.problem-text"),
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
    }
}