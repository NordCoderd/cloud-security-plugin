package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.kubernetes.quickfix.ReplaceValueToDefaultQuickFix
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLScalar

class InsecureProcMount : LocalInspectionTool() {
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
                    val procMountScalar =
                        YamlPath.findByYamlPath(
                            PROC_MOUNT_PATH,
                            container,
                        ) as? YAMLScalar? ?: continue

                    val value = procMountScalar.textValue
                    if (value != ALLOWED_VALUE) {
                        val descriptor =
                            HtmlProblemDescriptor(
                                procMountScalar,
                                SecurityPluginBundle.message("kube009.documentation"),
                                SecurityPluginBundle.message("kube009.problem-text"),
                                ProblemHighlightType.ERROR,
                                arrayOf(
                                    ReplaceValueToDefaultQuickFix(
                                        SecurityPluginBundle.message("kube009.qf.fix-value"),
                                    ),
                                ),
                            )
                        holder.registerProblem(descriptor)
                    }
                }
            }
        }
}

private const val PROC_MOUNT_PATH = "securityContext.procMount"
private const val ALLOWED_VALUE = "Default"
