package dev.protsenko.securityLinter.docker_compose

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import dev.protsenko.securityLinter.core.DockerFileConstants.PROHIBITED_PORTS
import dev.protsenko.securityLinter.core.DockerFileConstants.PROHIBITED_USERS
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.docker_compose.DockerComposeConstants.IMAGE_KEY_LITERAL
import dev.protsenko.securityLinter.docker_compose.DockerComposeConstants.PORTS_LITERAL
import dev.protsenko.securityLinter.docker_compose.DockerComposeConstants.PRIVILEGED_LITERAL
import dev.protsenko.securityLinter.docker_compose.DockerComposeConstants.USER_KEY_LITERAL
import dev.protsenko.securityLinter.docker_compose.DockerComposeConstants.supportedAttributes
import dev.protsenko.securityLinter.utils.PortUtils
import dev.protsenko.securityLinter.utils.image.ImageAnalyzer
import dev.protsenko.securityLinter.utils.image.ImageDefinitionCreator
import org.jetbrains.yaml.navigation.YAMLQualifiedNameProvider
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLKeyValue
import org.jetbrains.yaml.psi.YAMLSequenceItem

class DockerComposeInspection: LocalInspectionTool() {

    val provider = YAMLQualifiedNameProvider()

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor(){
            override fun visitFile(file: PsiFile) {
                if (file !is YAMLFile) return
                if (!file.name.startsWith("docker", ignoreCase = true)) return
                super.visitFile(file)
            }

            /**
             * For more information about service attributes: https://docs.docker.com/reference/compose-file/services
             */
            override fun visitElement(element: PsiElement) {
                if (element is YAMLKeyValue){
                    val fqn = provider.getQualifiedName(element) ?: return
                    if (!fqn.startsWith("services")) return

                    val attributeName = element.key?.text ?: return
                    if (attributeName !in supportedAttributes) return
                    val attributeValue = element.value?.text?.trim() ?: return

                    when (attributeName){
                        // Analyzing image definition
                        IMAGE_KEY_LITERAL -> {
                            val imageDefinition = ImageDefinitionCreator.fromString(attributeValue, emptyMap())
                            ImageAnalyzer.analyzeAndHighlight(imageDefinition, holder, element, emptyMap())
                        }
                        USER_KEY_LITERAL -> {
                            if (PROHIBITED_USERS.contains(attributeValue.trim())){
                                val descriptor = HtmlProblemDescriptor(
                                    element,
                                    SecurityPluginBundle.message("dfs002.documentation"),
                                    SecurityPluginBundle.message("dfs002.root-user-is-used"),
                                    ProblemHighlightType.ERROR
                                )

                                holder.registerProblem(descriptor)
                            }
                        }
                        PRIVILEGED_LITERAL -> {
                            if (attributeValue == "true"){
                                holder.registerProblem(
                                    element, SecurityPluginBundle.message("ds033.using-privileged"), ProblemHighlightType.ERROR
                                )
                            }
                        }
                        PORTS_LITERAL -> {
                           element.value?.children?.forEach {
                                if (it is YAMLSequenceItem){
                                    var portsDefinitions = it.value?.text ?: return@forEach
                                    // quotes at start and end should be trimmed
                                    if (portsDefinitions.length > 2){
                                        portsDefinitions = portsDefinitions.substring(1, portsDefinitions.length - 1)
                                        val containerPorts = PortUtils.parseContainerPorts(portsDefinitions)
                                        containerPorts.forEach { containerPort ->
                                            if (PROHIBITED_PORTS.contains(containerPort)){

                                                val descriptor = HtmlProblemDescriptor(
                                                    it,
                                                    SecurityPluginBundle.message("dfs011.documentation"),
                                                    SecurityPluginBundle.message("dfs011.ssh-port-exposed"),
                                                    ProblemHighlightType.ERROR,
                                                    emptyArray()
                                                )

                                                holder.registerProblem(descriptor)
                                            }
                                        }
                                    }
                                }
                                return@forEach
                            } ?: return
                        }
                        else -> return
                    }
                }
            }
        }
    }
}

object DockerComposeConstants {
    const val IMAGE_KEY_LITERAL = "image"
    const val USER_KEY_LITERAL = "user"
    const val PORTS_LITERAL = "ports"
    const val PRIVILEGED_LITERAL = "privileged"
    val supportedAttributes = setOf(
        IMAGE_KEY_LITERAL, USER_KEY_LITERAL,
        PORTS_LITERAL, PRIVILEGED_LITERAL
    )
}