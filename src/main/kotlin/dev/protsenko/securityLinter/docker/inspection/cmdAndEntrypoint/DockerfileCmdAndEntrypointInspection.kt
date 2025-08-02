package dev.protsenko.securityLinter.docker.inspection.cmdAndEntrypoint

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.docker.dockerFile.DockerPsiFile
import com.intellij.docker.dockerFile.parser.psi.*
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiElementVisitor.EMPTY_VISITOR
import com.intellij.psi.PsiFile
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.core.quickFix.DeletePsiElementQuickFix
import dev.protsenko.securityLinter.core.quickFix.ReplaceWithJsonNotationQuickFix

class DockerfileCmdAndEntrypointInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor {
        if (holder.file !is DockerPsiFile) {
            return EMPTY_VISITOR
        }
        return object : DockerFileVisitor() {
            override fun visitFile(file: PsiFile) {
                if (file !is DockerPsiFile) {
                    return
                }
                val fromCommands = file.findChildrenByClass(DockerFileFromCommand::class.java)
                val buildStages = fromCommands.associateBy { it.textOffset }

                val cmdCommands = file.findChildrenByClass(DockerFileCmdCommand::class.java).toList()
                cmdCommands.forEach { cmd ->
                    if (cmd.parametersInJsonForm == null) registerJsonNotationProblem(cmd)
                }
                val entrypoints = file.findChildrenByClass(DockerFileEntrypointCommand::class.java).toList()
                entrypoints.forEach { entrypoint ->
                    if (entrypoint.parametersInJsonForm == null) registerJsonNotationProblem(entrypoint)
                }

                verifyInstructions(cmdCommands, buildStages)
                verifyInstructions(entrypoints, buildStages)
            }

            private fun verifyInstructions(
                elementList: List<DockerPsiExecOrShellCommand>,
                buildStages: Map<Int, DockerFileFromCommand>,
            ) {
                val lastStage = buildStages.keys.maxOrNull() ?: return
                val lastInstructions =
                    elementList.filter {
                        it.textOffset > lastStage
                    }
                if (lastInstructions.size > 1) {
                    for (instruction in lastInstructions.dropLast(1)) {
                        val descriptor =
                            HtmlProblemDescriptor(
                                instruction,
                                SecurityPluginBundle.message("dsf004.documentation"),
                                SecurityPluginBundle.message("dfs004.only-one-cmd-or-entrypoint"),
                                ProblemHighlightType.ERROR,
                                arrayOf(DeletePsiElementQuickFix(SecurityPluginBundle.message("dfs004.remove-redundant-instruction"))),
                            )

                        holder.registerProblem(descriptor)
                    }
                }
            }

            private fun registerJsonNotationProblem(element: DockerPsiExecOrShellCommand) {
                val descriptor =
                    HtmlProblemDescriptor(
                        element,
                        SecurityPluginBundle.message("dfs005.documentation"),
                        SecurityPluginBundle.message("dfs005.use-json-notation"),
                        ProblemHighlightType.WARNING,
                        arrayOf(ReplaceWithJsonNotationQuickFix()),
                    )

                holder.registerProblem(descriptor)
            }
        }
    }
}
