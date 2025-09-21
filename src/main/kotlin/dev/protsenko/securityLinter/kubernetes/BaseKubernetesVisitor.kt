package dev.protsenko.securityLinter.kubernetes

import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.containerTypes
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.evaluateSpecPrefix
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.supportedKinds
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.YAMLUtil
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLSequence

abstract class BaseKubernetesVisitor : PsiElementVisitor() {
    override fun visitFile(file: PsiFile) {
        if (file !is YAMLFile) return

        val documents = file.documents

        for (document in documents) {
            val kind = YAMLUtil.getQualifiedKeyInDocument(document, listOf("kind")) ?: return
            val kindValue = kind.valueText
            if (kindValue !in supportedKinds) return

            val specPrefix = evaluateSpecPrefix(kindValue)
            analyze(specPrefix, document)
        }

        super.visitFile(file)
    }

    fun containers(
        specPrefix: String,
        document: YAMLDocument,
    ) = buildList {
        for (containerType in containerTypes) {
            val containers =
                YamlPath.findByYamlPath("${specPrefix}$containerType", document) as? YAMLSequence ?: continue

            addAll(
                containers.items.mapNotNull { containerItem ->
                    containerItem.value as? YAMLMapping
                },
            )
        }
    }

    abstract fun analyze(
        specPrefix: String,
        document: YAMLDocument,
    )
}
