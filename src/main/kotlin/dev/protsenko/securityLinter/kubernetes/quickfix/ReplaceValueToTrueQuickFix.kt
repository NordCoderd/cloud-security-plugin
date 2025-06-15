package dev.protsenko.securityLinter.kubernetes.quickfix

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.openapi.project.Project
import dev.protsenko.securityLinter.utils.PsiElementGenerator
import org.jetbrains.yaml.psi.YAMLScalar

class ReplaceValueToTrueQuickFix(@IntentionFamilyName private val message: String) : LocalQuickFix {
    override fun getFamilyName(): @IntentionFamilyName String = this.message

    override fun generatePreview(project: Project, previewDescriptor: ProblemDescriptor): IntentionPreviewInfo =
        IntentionPreviewInfo.EMPTY

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val psiElement = descriptor.psiElement as? YAMLScalar ?: return

        psiElement.replace(
            PsiElementGenerator.rawText(project, "true") ?: return
        )
    }
}