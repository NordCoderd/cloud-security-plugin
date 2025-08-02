package dev.protsenko.securityLinter.utils

import com.intellij.docker.dockerFile.DockerFileType
import com.intellij.docker.dockerFile.parser.psi.DockerFileFromCommand
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.childrenOfType

object PsiElementGenerator {
    const val DUMMY_DOCKERFILE_NAME = "Dockerfile"
    const val DUMMY_FILE_NAME = "dummy"

    inline fun <reified T : PsiElement> fromText(
        project: Project,
        text: String,
    ): T? {
        val fullDockerFileText = "FROM dummy\n$text"
        val factory = PsiFileFactory.getInstance(project)
        val dockerFile =
            factory.createFileFromText(DUMMY_DOCKERFILE_NAME, DockerFileType.DOCKER_FILE_TYPE, fullDockerFileText)
        return dockerFile.childrenOfType<T>().firstOrNull()
    }

    fun getDockerFileFromCommand(
        project: Project,
        image: String,
        digest: String,
        stageName: String?,
    ): DockerFileFromCommand? {
        val factory = PsiFileFactory.getInstance(project)
        val fromDefinition =
            if (stageName != null) {
                "FROM $image@$digest as $stageName"
            } else {
                "FROM $image@$digest"
            }
        val dockerFile =
            ApplicationManager.getApplication().runReadAction<PsiFile> {
                factory.createFileFromText(
                    DUMMY_DOCKERFILE_NAME,
                    DockerFileType.DOCKER_FILE_TYPE,
                    fromDefinition,
                )
            }
        return dockerFile.childrenOfType<DockerFileFromCommand>().firstOrNull()
    }

    fun rawText(
        project: Project,
        value: String,
    ): PsiElement? {
        val factory = PsiFileFactory.getInstance(project)
        val rawFile =
            factory.createFileFromText(
                DUMMY_FILE_NAME,
                PlainTextFileType.INSTANCE,
                value,
            )
        return rawFile.childrenOfType<PsiElement>().firstOrNull()
    }

    fun newLine(project: Project): PsiElement? {
        val factory = PsiFileFactory.getInstance(project)
        val emptyFile = factory.createFileFromText("raw", PlainTextFileType.INSTANCE, "\n")
        return emptyFile.firstChild
    }
}
