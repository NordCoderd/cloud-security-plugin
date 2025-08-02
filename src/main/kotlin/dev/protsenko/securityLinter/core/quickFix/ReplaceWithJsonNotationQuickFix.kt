package dev.protsenko.securityLinter.core.quickFix

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.docker.dockerFile.parser.psi.DockerFileCmdCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileEntrypointCommand
import com.intellij.docker.dockerFile.parser.psi.DockerPsiExecOrShellCommand
import com.intellij.openapi.project.Project
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.utils.PsiElementGenerator

class ReplaceWithJsonNotationQuickFix : LocalQuickFix {
    override fun getFamilyName(): @IntentionFamilyName String = SecurityPluginBundle.message("dfs005.replace-with-json-notation")

    override fun generatePreview(
        project: Project,
        previewDescriptor: ProblemDescriptor,
    ): IntentionPreviewInfo = IntentionPreviewInfo.EMPTY

    override fun applyFix(
        project: Project,
        problemDescriptor: ProblemDescriptor,
    ) {
        val psiElement = problemDescriptor.psiElement as? DockerPsiExecOrShellCommand ?: return
        val dockerCommand =
            when (psiElement) {
                is DockerFileCmdCommand -> "CMD"
                is DockerFileEntrypointCommand -> "ENTRYPOINT"
                else -> return
            }
        val shellCommand =
            psiElement.text
                .removePrefix(dockerCommand)
                .trim()
                .split("\\s+".toRegex())
                .joinToString(
                    prefix = "$dockerCommand [ ",
                    postfix = " ]",
                    separator = ",",
                ) { "\"${it}\"" }

        val psiCommand =
            PsiElementGenerator.fromText<DockerPsiExecOrShellCommand>(project, shellCommand) ?: return
        problemDescriptor.psiElement.replace(psiCommand)
    }
}
