package dev.protsenko.securityLinter.docker.inspection.run.impl

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.docker.dockerFile.parser.psi.DockerFileRunCommand
import com.intellij.psi.PsiElement
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.docker.inspection.run.core.DockerfileRunAnalyzer

class ArgumentsInRunCommandAnalyzer : DockerfileRunAnalyzer {
    override fun handle(
        runCommand: String,
        psiElement: PsiElement,
        holder: ProblemsHolder,
    ) {
        if (psiElement !is DockerFileRunCommand) return
        if (psiElement.variableRefSimpleList.isNotEmpty()) {
            val descriptor =
                HtmlProblemDescriptor(
                    psiElement,
                    SecurityPluginBundle.message("dfs016.documentation"),
                    SecurityPluginBundle.message("dfs016.avoid-arg-in-run"),
                    ProblemHighlightType.ERROR,
                )

            holder.registerProblem(descriptor)
        }
    }
}
