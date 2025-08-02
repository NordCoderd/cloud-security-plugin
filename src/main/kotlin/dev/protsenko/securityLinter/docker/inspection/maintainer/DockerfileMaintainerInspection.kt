package dev.protsenko.securityLinter.docker.inspection.maintainer

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.codeInspection.*
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.docker.dockerFile.DockerPsiFile
import com.intellij.docker.dockerFile.parser.psi.DockerFileLabelCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileMaintainerCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileVisitor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiElementVisitor.EMPTY_VISITOR
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.utils.PsiElementGenerator

class DockerfileMaintainerInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor {
        if (holder.file !is DockerPsiFile) {
            return EMPTY_VISITOR
        }
        return object : DockerFileVisitor() {
            override fun visitMaintainerCommand(o: DockerFileMaintainerCommand) {
                val descriptor =
                    HtmlProblemDescriptor(
                        o,
                        SecurityPluginBundle.message("dfs013.documentation"),
                        SecurityPluginBundle.message("dfs013.no-maintainer"),
                        ProblemHighlightType.LIKE_DEPRECATED,
                        arrayOf(ReplaceMaintainerWithLabel()),
                    )

                holder.registerProblem(descriptor)
            }
        }
    }

    private class ReplaceMaintainerWithLabel : LocalQuickFix {
        override fun generatePreview(
            project: Project,
            previewDescriptor: ProblemDescriptor,
        ): IntentionPreviewInfo = IntentionPreviewInfo.EMPTY

        override fun getFamilyName(): @IntentionFamilyName String = SecurityPluginBundle.message("dfs013.replace-maintainer")

        override fun applyFix(
            project: Project,
            descriptor: ProblemDescriptor,
        ) {
            val maintainer = descriptor.psiElement.text
            val authorText = maintainer.replace("MAINTAINER ", AUTHOR_LABEL).trim() + "\""
            val psiAuthor =
                PsiElementGenerator.fromText<DockerFileLabelCommand>(project, authorText) ?: return
            descriptor.psiElement.replace(psiAuthor)
        }
    }
}

private const val AUTHOR_LABEL = "LABEL org.opencontainers.image.authors=\""
