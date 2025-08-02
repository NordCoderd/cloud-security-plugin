package dev.protsenko.securityLinter.docker.inspection.copyAndAdd

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.docker.dockerFile.DockerPsiFile
import com.intellij.docker.dockerFile.parser.psi.DockerFileAddOrCopyCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileVisitor
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiElementVisitor.EMPTY_VISITOR
import dev.protsenko.securityLinter.docker.inspection.copyAndAdd.core.DockerfileCopyOrAddAnalyzer

class DockerfileCopyAndAddInspection : LocalInspectionTool() {
    val extensionPointName =
        ExtensionPointName.create<DockerfileCopyOrAddAnalyzer>("dev.protsenko.security-linter.dockerFileCopyOrAddAnalyzer")

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor {
        if (holder.file !is DockerPsiFile) {
            return EMPTY_VISITOR
        }

        return object : DockerFileVisitor() {
            val extensions = extensionPointName.extensions

            override fun visitAddOrCopyCommand(o: DockerFileAddOrCopyCommand) {
                for (extension in extensions) {
                    extension.handle(o, holder)
                }
            }
        }
    }
}
