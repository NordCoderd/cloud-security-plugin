package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.kubernetes.quickfix.ReplaceValueToBlankQuickFix
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLMapping

class SettingSELinuxInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : BaseKubernetesVisitor() {
            override fun analyze(specPrefix: String, document: YAMLDocument) {
                val seLinuxOptionsMapping = YamlPath.findByYamlPath(
                    "$specPrefix$SELINUX_OPTIONS_OVERRIDE",
                    document
                ) as? YAMLMapping

                highlightIfProblem(seLinuxOptionsMapping)

                val containers = containers(specPrefix, document)
                for (container in containers) {
                    val seLinuxOptionsMapping = YamlPath.findByYamlPath(
                        SELINUX_OPTIONS_PATH,
                        container
                    ) as? YAMLMapping ?: continue

                    highlightIfProblem(seLinuxOptionsMapping)
                }
            }


            private fun highlightIfProblem(seLinuxOptions: YAMLMapping?) {
                seLinuxOptions ?: return

                val typeValue = seLinuxOptions.getKeyValueByKey(SELINUX_OPTIONS_TYPE)
                if (typeValue != null && typeValue.value !=null && typeValue.valueText !in allowedTypes) {
                    val descriptor = HtmlProblemDescriptor(
                        typeValue.value!!,
                        SecurityPluginBundle.message("kube008.documentation"),
                        SecurityPluginBundle.message("kube008.setting-selinux-is-restricted"),
                        ProblemHighlightType.ERROR, arrayOf(
                            ReplaceValueToBlankQuickFix(
                                SecurityPluginBundle.message("kube008.qf.fix-value"),
                            )
                        )
                    )
                    holder.registerProblem(descriptor)
                }

                val userValue = seLinuxOptions.getKeyValueByKey(SELINUX_OPTIONS_USER)
                val roleValue = seLinuxOptions.getKeyValueByKey(SELINUX_OPTIONS_ROLE)
                for (value in listOf(userValue, roleValue)) {
                    if (value != null&& value.value != null && value.valueText.isNotBlank()) {
                        val descriptor = HtmlProblemDescriptor(
                            value.value!!,
                            SecurityPluginBundle.message("kube008.documentation"),
                            SecurityPluginBundle.message("kube008.setting-selinux-is-restricted"),
                            ProblemHighlightType.ERROR, arrayOf(
                                ReplaceValueToBlankQuickFix(
                                    SecurityPluginBundle.message("kube008.qf.fix-value"),
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

private const val SELINUX_OPTIONS_PATH = "securityContext.seLinuxOptions"
private const val SELINUX_OPTIONS_OVERRIDE = "spec.$SELINUX_OPTIONS_PATH"
private const val SELINUX_OPTIONS_TYPE = "type"
private const val SELINUX_OPTIONS_USER = "user"
private const val SELINUX_OPTIONS_ROLE = "role"

private val allowedTypes = setOf(
    "container_t",
    "container_init_t",
    "container_kvm_t",
    "container_engine_t",
    ""
)
