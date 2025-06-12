package dev.protsenko.securityLinter.core

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.testFramework.TestDataPath
import org.jetbrains.yaml.psi.YAMLFile

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
abstract class KubernetesHighlightingBaseTest(): InspectionBaseTest() {
    override fun getTestDataPath() = "src/test/testData/kubernetes"

    override val targetFileName = "kube-object.yml"
    override val expectedFile: Class<*> = YAMLFile::class.java

    abstract override val targetInspection: LocalInspectionTool
}