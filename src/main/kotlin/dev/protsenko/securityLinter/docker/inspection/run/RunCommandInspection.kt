// package dev.protsenko.securityLinter.docker.inspection.run
//
// import com.intellij.codeInspection.LocalInspectionTool
// import com.intellij.codeInspection.ProblemHighlightType
// import com.intellij.codeInspection.ProblemsHolder
// import com.intellij.docker.dockerFile.parser.psi.DockerFileRunCommand
// import com.intellij.docker.dockerFile.parser.psi.DockerFileVisitor
// import com.intellij.openapi.extensions.ExtensionPointName
// import com.intellij.psi.PsiElement
// import com.intellij.psi.PsiElementVisitor
// import dev.protsenko.securityLinter.core.SecurityPluginBundle
// import dev.protsenko.securityLinter.docker.inspection.run.core.DockerfileRunAnalyzer
//
// const val MALICIOUS_RM_RF_COMMAND = "rm -rf /"
//
// interface DockerfileRunAnalyzer {
//    fun handle(
//        runCommand: String,
//        psiElement: PsiElement,
//        holder: ProblemsHolder,
//    )
// }
//
// class RmRfAnalyzer : DockerfileRunAnalyzer {
//    override fun handle(
//        runCommand: String,
//        psiElement: PsiElement,
//        holder: ProblemsHolder,
//    ) {
//        if (runCommand.contains(MALICIOUS_RM_RF_COMMAND)) {
//            holder.registerProblem(
//                psiElement,
//                SecurityPluginBundle.message("inspection.text"),
//                ProblemHighlightType.ERROR,
//            )
//        }
//    }
// }
//
// class RunCommandInspection : LocalInspectionTool() {
//    override fun buildVisitor(
//        holder: ProblemsHolder,
//        isOnTheFly: Boolean,
//    ): PsiElementVisitor =
//        object : DockerFileVisitor() {
//            override fun visitRunCommand(o: DockerFileRunCommand) {
//                val extensionPointName =
//                    ExtensionPointName.create<DockerfileRunAnalyzer>("dev.protsenko.security-linter.dockerFileRunAnalyzer")
//
//                val runCommand = o.text
//
//                for (extension in extensionPointName.extensions) {
//                    extension.handle(runCommand, o, holder)
//                }
//            }
//        }
// }
