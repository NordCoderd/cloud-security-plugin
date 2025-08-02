package dev.protsenko.securityLinter.docker.inspection.copyAndAdd.impl

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.docker.dockerFile.DockerPsiFile
import com.intellij.docker.dockerFile.parser.psi.DockerFileAddOrCopyCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileFromCommand
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.core.quickFix.DeletePsiElementQuickFix
import dev.protsenko.securityLinter.docker.inspection.copyAndAdd.core.DockerfileCopyOrAddAnalyzer

class CopyReferringToCurrentImageAnalyzer : DockerfileCopyOrAddAnalyzer {
    override fun handle(
        element: DockerFileAddOrCopyCommand,
        holder: ProblemsHolder,
    ) {
        val currentFile = element.parent as? DockerPsiFile ?: return
        val currentStep =
            currentFile
                .findChildrenByClass(DockerFileFromCommand::class.java)
                .lastOrNull { it.textOffset < element.textOffset }
                ?.fromStageDeclaration
                ?.declaredName
                ?.text

        if (element.addKeyword != null) return
        val copyFromOption =
            element.regularOptionList
                .filter { regularOption ->
                    val optionName = regularOption.optionName ?: return@filter false
                    optionName.text == COPY_FROM_OPTION_NAME
                }.firstOrNull() ?: return

        val step = copyFromOption.regularValue?.text ?: return

        if (step == currentStep) {
            val descriptor =
                HtmlProblemDescriptor(
                    copyFromOption,
                    SecurityPluginBundle.message("dfs006.documentation"),
                    SecurityPluginBundle.message("dfs006.copy-referring-to-the-current-image"),
                    ProblemHighlightType.ERROR,
                    arrayOf(
                        DeletePsiElementQuickFix(
                            SecurityPluginBundle.message("dfs006.remove-referring"),
                        ),
                    ),
                )

            holder.registerProblem(descriptor)
        }
    }
}

private const val COPY_FROM_OPTION_NAME = "from"
