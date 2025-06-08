package dev.protsenko.securityLinter.rego

import com.fasterxml.jackson.core.type.TypeReference
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.styra.opa.wasm.DefaultMappers
import dev.protsenko.security.linter.rules.model.EvalEnvelope
import org.jetbrains.yaml.psi.YAMLFile

class RegoBasedInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                if (file !is YAMLFile || !file.isValid) {
                    return
                }
                val start = System.currentTimeMillis()
                val fileContent = file.text

                val rawInput = DefaultMappers.jsonMapper.createArrayNode();
                rawInput.add(DefaultMappers.yamlMapper.readTree(fileContent));

                val alertMessagesList = buildList {
                    val policies = KubernetesNsaRules.policies
                    policies.forEach { policy ->
                        val evalResult = policy.evaluate(rawInput)
                        val alerts: List<EvalEnvelope> = DefaultMappers.jsonMapper.readValue(
                            evalResult,
                            object : TypeReference<List<EvalEnvelope>>() {}
                        )
                        val alertMessages = alerts.mapNotNull {
                            it.result?.mapNotNull { res ->
                                res.alertMessage
                            }
                        }.flatten()

                        addAll(alertMessages)
                    }
                }

                alertMessagesList.forEach {
                    holder.registerProblem(
                        file,
                        it,
                        ProblemHighlightType.WARNING)
                }

                println("Time: ${System.currentTimeMillis() - start}ms")

                super.visitFile(file)
            }
        }
    }

}

