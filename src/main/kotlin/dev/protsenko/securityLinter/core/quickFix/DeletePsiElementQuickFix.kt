package dev.protsenko.securityLinter.core.quickFix

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.openapi.project.Project

class DeletePsiElementQuickFix(
    @IntentionFamilyName private val message: String,
) : LocalQuickFix {
    override fun getFamilyName(): @IntentionFamilyName String = this.message

    override fun generatePreview(
        project: Project,
        previewDescriptor: ProblemDescriptor,
    ): IntentionPreviewInfo = IntentionPreviewInfo.EMPTY

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        descriptor.psiElement.delete()
    }
}
