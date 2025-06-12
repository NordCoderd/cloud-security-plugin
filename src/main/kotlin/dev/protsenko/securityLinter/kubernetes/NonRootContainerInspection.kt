package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.YAMLUtil
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLScalar
import org.jetbrains.yaml.psi.YAMLSequence

class NonRootContainerInspection : LocalInspectionTool() {

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

                    val isRunAsNonRoot = YamlPath.findByYamlPath("${specPrefix}spec.$RUN_AS_NON_ROOT", document)
                    val isAllowedRunAsNonRoot = !highlightIfValueNotTrue(isRunAsNonRoot, holder)

                    val isRunAsUser = YamlPath.findByYamlPath("${specPrefix}spec.$RUN_AS_USER", document)
                    val isRunAsGroup = YamlPath.findByYamlPath("${specPrefix}spec.$RUN_AS_GROUP", document)
                    highlightIfValueZero(isRunAsUser, holder)
                    highlightIfValueZero(isRunAsGroup, holder)

                    // container level
                    var allContainersAreNonRoot = true

                    for (containerType in containerTypes) {
                        val containers =
                            YamlPath.findByYamlPath("${specPrefix}$containerType", document) as? YAMLSequence ?: continue

                        for (containerItem in containers.items) {
                            val containerYaml = containerItem.value as? YAMLMapping ?: continue

                            val isRunAsUser =
                                YamlPath.findByYamlPath(RUN_AS_USER, containerYaml)

                            val isRunAsGroup =
                                YamlPath.findByYamlPath(RUN_AS_GROUP, containerYaml)

                            highlightIfValueZero(isRunAsUser, holder)
                            highlightIfValueZero(isRunAsGroup, holder)

                            val isRunAsNonRootContainer =
                                YamlPath.findByYamlPath(RUN_AS_NON_ROOT, containerYaml)

                            //The container fields may be undefined/nil if the
                            // pod-level spec.securityContext.runAsNonRoot is set to true.

                            // global and container level isn't set
                            if (isRunAsNonRoot == null && isRunAsNonRootContainer == null){
                                allContainersAreNonRoot = false
                            }

                            val isRestrictedValue = highlightIfValueNotTrue(isRunAsNonRootContainer, holder)
                            if (isRestrictedValue) {
                                allContainersAreNonRoot = false
                            }
                        }
                    }

                    if (isRunAsNonRoot == null && !allContainersAreNonRoot) {
                        val descriptor = HtmlProblemDescriptor(
                            kind,
                            SecurityPluginBundle.message("kube001.documentation"),
                            SecurityPluginBundle.message("kube001.non-root-containers"),
                            ProblemHighlightType.ERROR, emptyArray()
                        )

                        holder.registerProblem(descriptor)
                    }
                }

                super.visitFile(file)
            }
        }
    }

    private fun evaluateSpecPrefix(kind: String): String {
        if (kind == "Pod") return ""
        if (kind == "CronJob") return "spec.jobTemplate.spec.template."
        if (kind in specInTemplateKindTypes) return "spec.template."
        return ""
    }

    private fun highlightIfValueNotTrue(element: PsiElement?, holder: ProblemsHolder): Boolean {
        if (element !is YAMLScalar) return false
        val isRunAsNonRoot = element.textValue.toBooleanStrictOrNull()

        if (isRunAsNonRoot != true) {
            val descriptor = HtmlProblemDescriptor(
                element,
                SecurityPluginBundle.message("kube001.documentation"),
                SecurityPluginBundle.message("kube001.non-root-containers"),
                ProblemHighlightType.ERROR, emptyArray()
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
                SecurityPluginBundle.message("kube001.non-root-containers"),
                ProblemHighlightType.ERROR, emptyArray()
            )

            holder.registerProblem(descriptor)
        }
    }

}

private val specInTemplateKindTypes = setOf("Deployment", "ReplicaSet", "DaemonSet", "StatefulSet", "Job")
private val supportedKinds = specInTemplateKindTypes + setOf("Pod", "CronJob")

private val containerTypes = listOf("spec.containers", "spec.initContainers", "spec.ephemeralContainers")

private const val RUN_AS_NON_ROOT = "securityContext.runAsNonRoot"
private const val RUN_AS_USER = "securityContext.runAsUser"
private const val RUN_AS_GROUP = "securityContext.runAsGroup"
