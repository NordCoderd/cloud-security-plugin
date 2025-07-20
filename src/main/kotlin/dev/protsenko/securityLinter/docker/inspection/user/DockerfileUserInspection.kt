package dev.protsenko.securityLinter.docker.inspection.user

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.codeInspection.*
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.docker.dockerFile.DockerPsiFile
import com.intellij.docker.dockerFile.parser.psi.DockerFileFromCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileUserCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileVisitor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiElementVisitor.EMPTY_VISITOR
import com.intellij.psi.PsiFile
import dev.protsenko.securityLinter.core.DockerFileConstants.PROHIBITED_USERS
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.utils.DockerPsiAnalyzer
import dev.protsenko.securityLinter.utils.PsiElementGenerator
import dev.protsenko.securityLinter.utils.resolveVariable

class DockerfileUserInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor {
        if (holder.file !is DockerPsiFile) {
            return EMPTY_VISITOR
        }

        return object : DockerFileVisitor() {
            override fun visitFile(file: PsiFile) {
                if (file !is DockerPsiFile) return

                val buildStages = file.findChildrenByClass(DockerFileFromCommand::class.java)
                    .associateBy { it.textOffset }
                val resolvedUsers = file.findChildrenByClass(DockerFileUserCommand::class.java)
                    .associateBy { it.textOffset }

                val lastStageOffset = buildStages.keys.maxOrNull() ?: return
                val lastUserOffset = resolvedUsers.keys.maxOrNull()

                val lastStage = buildStages[lastStageOffset] ?: return
                if (lastUserOffset == null || lastUserOffset < lastStageOffset) {
                    val descriptor = HtmlProblemDescriptor(
                        lastStage,
                        SecurityPluginBundle.message("dfs002.documentation"),
                        SecurityPluginBundle.message("dfs002.missing-user"),
                        ProblemHighlightType.ERROR, arrayOf(ReplaceOrAddUserQuickFix(replace = false))
                    )
                    holder.registerProblem(descriptor)
                    return
                }

                val lastUser = resolvedUsers[lastUserOffset] ?: return
                val variables = lastUser.variableRefSimpleList

                val username = if (variables.isNotEmpty()) {
                    val descriptor = HtmlProblemDescriptor(
                        lastUser,
                        SecurityPluginBundle.message("dfs002.documentation"),
                        SecurityPluginBundle.message("dfs002.arg-in-user"),
                        ProblemHighlightType.ERROR, arrayOf(ReplaceOrAddUserQuickFix(replace = true))
                    )
                    holder.registerProblem(descriptor)

                    val referencedNameVariable = variables.firstOrNull() ?: return
                    referencedNameVariable.resolveVariable()
                } else {
                    val commandParts = DockerPsiAnalyzer.splitCommand(lastUser)
                    if (commandParts.size != 2) return
                    commandParts.lastOrNull()
                }

                if (username != null && PROHIBITED_USERS.contains(username)) {
                    val descriptor = HtmlProblemDescriptor(
                        lastUser,
                        SecurityPluginBundle.message("dfs002.documentation"),
                        SecurityPluginBundle.message("dfs002.root-user-is-used"),
                        ProblemHighlightType.ERROR, arrayOf(ReplaceOrAddUserQuickFix(replace = true))
                    )

                    holder.registerProblem(descriptor)
                }
            }
        }
    }

    private class ReplaceOrAddUserQuickFix(private val replace: Boolean) : LocalQuickFix {
        companion object {
            const val USER_NOBODY = "USER nobody"
        }

        override fun generatePreview(project: Project, previewDescriptor: ProblemDescriptor): IntentionPreviewInfo =
            IntentionPreviewInfo.EMPTY

        override fun getFamilyName(): @IntentionFamilyName String = if (replace) {
            SecurityPluginBundle.message("dfs002.replace-root-with-nobody")
        } else {
            SecurityPluginBundle.message("dfs002.add-nobody-user")
        }

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val nobodyElement = PsiElementGenerator.fromText<DockerFileUserCommand>(project, USER_NOBODY) ?: return
            if (replace) {
                descriptor.psiElement.replace(nobodyElement)
            } else {
                val newLine = PsiElementGenerator.newLine(project) ?: return
                val targetElement = descriptor.psiElement
                val parentElement = targetElement.parent ?: return
                parentElement.add(newLine)
                parentElement.add(nobodyElement)
            }
        }
    }
}