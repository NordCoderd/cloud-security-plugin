package dev.protsenko.securityLinter.utils.image

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.docker.dockerFile.parser.psi.DockerFileFromCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileFromStageDeclaration
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.utils.PsiElementGenerator
import org.jetbrains.yaml.psi.YAMLKeyValue

class ReplaceTagWithDigestQuickFix(
    private val imageName: String,
) : LocalQuickFix,
    HighPriorityAction {
    override fun getFamilyName(): @IntentionFamilyName String = SecurityPluginBundle.message("dfs001.lookup-for-digest")

    override fun generatePreview(
        project: Project,
        previewDescriptor: ProblemDescriptor,
    ): IntentionPreviewInfo = IntentionPreviewInfo.EMPTY

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        DockerImageDigestFetcher
            .fetchDigest(imageName)
            .thenAccept { digest ->
                ApplicationManager.getApplication().runReadAction {
                    replaceImageName(project, descriptor, digest)
                }
            }.exceptionally { throwable ->
                ApplicationManager.getApplication().invokeLater {
                    notifyError(project, "Failed to fetch digest for image '$imageName': ${throwable.message}")
                }
                null
            }
    }

    private fun replaceImageName(
        project: Project,
        descriptor: ProblemDescriptor,
        digest: String,
    ) {
        val targetElement = descriptor.psiElement
        if (targetElement is DockerFileFromCommand) {
            val buildStageName = retrieveBuildStageName(targetElement)
            val dockerFileFromCommand =
                PsiElementGenerator.getDockerFileFromCommand(project, imageName, digest, buildStageName) ?: return
            ApplicationManager.getApplication().invokeLater {
                WriteCommandAction.runWriteCommandAction(project) {
                    targetElement.replace(dockerFileFromCommand)
                }
            }
        } else if (targetElement is YAMLKeyValue) {
            val imageDefinitionWithDigest =
                PsiElementGenerator.rawText(project, "$imageName@$digest") ?: return
            val targetValueElement = targetElement.value ?: return
            ApplicationManager.getApplication().invokeLater {
                WriteCommandAction.runWriteCommandAction(project) {
                    targetValueElement.replace(imageDefinitionWithDigest)
                }
            }
        }
    }

    private fun retrieveBuildStageName(element: PsiElement): String? {
        if (element !is DockerFileFromCommand) return null
        return element
            .children
            .filterIsInstance<DockerFileFromStageDeclaration>()
            .firstOrNull()
            ?.declaredName
            ?.text
    }

    fun notifyError(
        project: Project,
        content: String,
    ) {
        NotificationGroupManager
            .getInstance()
            .getNotificationGroup("dev.protsenko.securityLinter")
            .createNotification(content, NotificationType.ERROR)
            .notify(project)
    }
}
