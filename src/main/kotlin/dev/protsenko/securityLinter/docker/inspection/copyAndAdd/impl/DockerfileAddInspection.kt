package dev.protsenko.securityLinter.docker.inspection.copyAndAdd.impl

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.docker.dockerFile.parser.psi.DockerFileAddOrCopyCommand
import com.intellij.openapi.project.Project
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.docker.inspection.copyAndAdd.core.DockerfileCopyOrAddAnalyzer
import dev.protsenko.securityLinter.utils.PsiElementGenerator
import dev.protsenko.securityLinter.utils.extension
import dev.protsenko.securityLinter.utils.removeQuotes

class DockerfileAddInspection : DockerfileCopyOrAddAnalyzer {
    override fun handle(
        element: DockerFileAddOrCopyCommand,
        holder: ProblemsHolder,
    ) {
        if (element.copyKeyword != null) return

        val filesFromJson =
            element.parametersInJsonForm?.children?.map { it.text.removeQuotes() } ?: emptyList()
        val filesFromList = element.fileOrUrlList.map { it.text.removeQuotes() }

        val files =
            if (filesFromJson.isNotEmpty() && filesFromJson.size >= 2) {
                filesFromJson.subList(0, filesFromJson.size - 1)
            } else if (filesFromList.isNotEmpty() && filesFromList.size >= 2) {
                filesFromList.subList(0, filesFromList.size - 1)
            } else {
                null
            } ?: return

        for (fileName in files) {
            if (fileName.extension() != GZ_FILE_EXTENSION) {
                val descriptor =
                    HtmlProblemDescriptor(
                        element,
                        SecurityPluginBundle.message("dfs007.documentation"),
                        SecurityPluginBundle.message("dfs007.add-instead-copy"),
                        ProblemHighlightType.ERROR,
                        arrayOf(ReplaceAddWithCopyQuickFix()),
                    )

                holder.registerProblem(descriptor)
                return
            }
        }
    }

    private class ReplaceAddWithCopyQuickFix : LocalQuickFix {
        override fun getFamilyName(): @IntentionFamilyName String = SecurityPluginBundle.message("dfs007.replace-add-with-copy")

        override fun generatePreview(
            project: Project,
            previewDescriptor: ProblemDescriptor,
        ): IntentionPreviewInfo = IntentionPreviewInfo.EMPTY

        override fun applyFix(
            project: Project,
            problemDescriptor: ProblemDescriptor,
        ) {
            val problemElement = problemDescriptor.psiElement
            val addCommand = problemElement.text
            if (!addCommand.startsWith(ADD_KEYWORD)) return

            val copyCommand = addCommand.replaceFirst(ADD_KEYWORD, COPY_KEYWORD)
            val copyPsiElement =
                PsiElementGenerator.fromText<DockerFileAddOrCopyCommand>(project, copyCommand)
                    ?: return
            problemElement.replace(copyPsiElement)
        }
    }
}

private const val GZ_FILE_EXTENSION = "gz"
private const val ADD_KEYWORD = "ADD"
private const val COPY_KEYWORD = "COPY"
