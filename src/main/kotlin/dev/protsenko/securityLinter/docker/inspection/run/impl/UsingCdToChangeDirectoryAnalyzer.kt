package dev.protsenko.securityLinter.docker.inspection.run.impl

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.docker.inspection.run.core.DockerfileRunAnalyzer
import dev.protsenko.securityLinter.utils.DockerPsiAnalyzer

class UsingCdToChangeDirectoryAnalyzer : DockerfileRunAnalyzer {
    override fun handle(
        runCommand: String,
        psiElement: PsiElement,
        holder: ProblemsHolder,
    ) {
        if (!runCommand.contains("cd")) return
        val commandParts = DockerPsiAnalyzer.splitCommand(runCommand)
        if (commandParts.size < 2) return

        if (commandParts[1] == "cd") {
            val descriptor =
                HtmlProblemDescriptor(
                    psiElement,
                    SecurityPluginBundle.message("dfs023.documentation"),
                    SecurityPluginBundle.message("dfs023.use-workdir-over-cd"),
                    ProblemHighlightType.WEAK_WARNING,
                    emptyArray(),
                )

            holder.registerProblem(descriptor)
        }
    }
}
