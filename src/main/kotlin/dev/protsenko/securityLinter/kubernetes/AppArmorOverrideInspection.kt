package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.kubernetes.quickfix.ReplaceValueToRuntimeDefaultQuickFix
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLScalar

class AppArmorOverrideInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor =
        object : BaseKubernetesVisitor() {
            override fun analyze(
                specPrefix: String,
                document: YAMLDocument,
            ) {
                val appArmorProfileType =
                    YamlPath.findByYamlPath(
                        "$specPrefix$APP_ARMOR_PROFILE_TYPE",
                        document,
                    ) as? YAMLScalar

                highlightIfProblem(appArmorProfileType)

                val containers = containers(specPrefix, document)
                for (container in containers) {
                    val appArmorProfileType =
                        YamlPath.findByYamlPath(
                            APP_ARMOR_PROFILE_PATH,
                            container,
                        ) as? YAMLScalar ?: continue

                    highlightIfProblem(appArmorProfileType)
                }
            }

            // TODO: verify metadata (in docs in beta version)
            private fun highlightIfProblem(appArmorProfileType: YAMLScalar?) {
                if (appArmorProfileType != null && appArmorProfileType.textValue !in allowedProfiles) {
                    val descriptor =
                        HtmlProblemDescriptor(
                            appArmorProfileType,
                            SecurityPluginBundle.message("kube007.documentation"),
                            SecurityPluginBundle.message("kube007.problem-text"),
                            ProblemHighlightType.ERROR,
                            arrayOf(
                                ReplaceValueToRuntimeDefaultQuickFix(
                                    SecurityPluginBundle.message("kube007.qf.fix-value"),
                                ),
                            ),
                        )

                    holder.registerProblem(descriptor)
                }
            }
        }
}

private const val APP_ARMOR_PROFILE_PATH = "securityContext.appArmorProfile.type"
private const val APP_ARMOR_PROFILE_TYPE = "spec.$APP_ARMOR_PROFILE_PATH"
private val allowedProfiles = setOf("RuntimeDefault", "Localhost", "")
