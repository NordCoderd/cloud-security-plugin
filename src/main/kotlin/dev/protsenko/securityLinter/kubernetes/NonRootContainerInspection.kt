package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.kubernetes.quickfix.ReplaceValueTo1000QuickFix
import dev.protsenko.securityLinter.kubernetes.quickfix.ReplaceValueToTrueQuickFix
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.RUN_AS_GROUP
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.RUN_AS_NON_ROOT
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.RUN_AS_USER
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLScalar

class NonRootContainerInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : BaseKubernetesVisitor() {
            override fun analyze(specPrefix: String, document: YAMLDocument) {
                val isRunAsNonRoot = YamlPath.findByYamlPath("${specPrefix}spec.$RUN_AS_NON_ROOT", document)
                highlightIfValueNotTrue(isRunAsNonRoot, holder)

                val isRunAsUser = YamlPath.findByYamlPath("${specPrefix}spec.$RUN_AS_USER", document)
                val isRunAsGroup = YamlPath.findByYamlPath("${specPrefix}spec.$RUN_AS_GROUP", document)
                highlightIfValueZero(isRunAsUser, holder)
                highlightIfValueZero(isRunAsGroup, holder)

                val containers = containers(specPrefix, document)

                for (container in containers) {
                    val isRunAsUser =
                        YamlPath.findByYamlPath(RUN_AS_USER, container)

                    val isRunAsGroup =
                        YamlPath.findByYamlPath(RUN_AS_GROUP, container)

                    highlightIfValueZero(isRunAsUser, holder)
                    highlightIfValueZero(isRunAsGroup, holder)

                    val isRunAsNonRootContainer =
                        YamlPath.findByYamlPath(RUN_AS_NON_ROOT, container)

                    highlightIfValueNotTrue(isRunAsNonRootContainer, holder)
                }
            }
        }
    }

    private fun highlightIfValueNotTrue(element: PsiElement?, holder: ProblemsHolder): Boolean {
        if (element !is YAMLScalar) return false
        val isRunAsNonRoot = element.textValue.toBooleanStrictOrNull()

        if (isRunAsNonRoot != true) {
            val descriptor = HtmlProblemDescriptor(
                element,
                SecurityPluginBundle.message("kube001.documentation"),
                SecurityPluginBundle.message("kube001.problem-text"),
                ProblemHighlightType.ERROR, arrayOf(
                    ReplaceValueToTrueQuickFix(SecurityPluginBundle.message("kube001.qf.fix-run-as-non-root"))
                )
            )

            holder.registerProblem(descriptor)
            return true
        }
        return false
    }

    // Running as Non-root user (v1.23+)
    private fun highlightIfValueZero(element: PsiElement?, holder: ProblemsHolder) {
        if (element !is YAMLScalar) return
        val isRunAsUser = element.textValue.toIntOrNull() ?: return

        if (isRunAsUser == 0) {
            val descriptor = HtmlProblemDescriptor(
                element,
                SecurityPluginBundle.message("kube001.documentation"),
                SecurityPluginBundle.message("kube001.problem-text"),
                ProblemHighlightType.ERROR, arrayOf(
                    ReplaceValueTo1000QuickFix(SecurityPluginBundle.message("kube001.qf.fix-run-as-user-or-group"))
                )
            )

            holder.registerProblem(descriptor)
        }
    }

}