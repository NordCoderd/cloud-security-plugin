package dev.protsenko.securityLinter.utils.image

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.docker.agent.DockerRepoTag
import com.intellij.psi.PsiElement
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle

object ImageAnalyzer {
    fun analyzeAndHighlight(
        imageDefinition: ImageDefinition,
        holder: ProblemsHolder,
        element: PsiElement,
        aliasToImageDefinition: Map<String, ImageDefinition>,
    ) {
        val effectiveImageDefinition = aliasToImageDefinition[imageDefinition.imageName] ?: imageDefinition

        if (effectiveImageDefinition.version == null) {
            val descriptor =
                HtmlProblemDescriptor(
                    element,
                    SecurityPluginBundle.message("dfs001.documentation"),
                    SecurityPluginBundle.message("dfs001.missing-version-tag"),
                    ProblemHighlightType.ERROR,
                    arrayOf(ReplaceTagWithDigestQuickFix(effectiveImageDefinition.imageName)),
                )

            holder.registerProblem(descriptor)
        } else if (effectiveImageDefinition.version == DockerRepoTag.DEFAULT_TAG) {
            val descriptor =
                HtmlProblemDescriptor(
                    element,
                    SecurityPluginBundle.message("dfs001.documentation"),
                    SecurityPluginBundle.message("dfs001.latest-tag"),
                    ProblemHighlightType.ERROR,
                    arrayOf(ReplaceTagWithDigestQuickFix(effectiveImageDefinition.imageName)),
                )

            holder.registerProblem(descriptor)
        }
    }
}
