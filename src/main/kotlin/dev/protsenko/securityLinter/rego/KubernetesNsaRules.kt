package dev.protsenko.securityLinter.rego

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.styra.opa.wasm.DefaultMappers
import java.io.File
import com.styra.opa.wasm.OpaPolicy


object KubernetesNsaRules {
    private const val POLICY_FOLDER = "nsa_rules"
    private const val CONTENT_FILE_NAME = "content"
    private val classLoader = KubernetesNsaRules::class.java.classLoader
    val policies = hashSetOf<OpaPolicy>()

    init {
        listOfPolicyFiles().forEach {
            val policy = loadPolicyFromResources("$POLICY_FOLDER/$it")
            if (policy != null) {
                policies.add(
                    OpaPolicy
                        .builder()
                        .withPolicy(policy)
                        .build()
                )
            }
        }
    }

    private fun listOfPolicyFiles(): List<String> {
        val res = loadPolicyFromResources("$POLICY_FOLDER/$CONTENT_FILE_NAME")
        if (res == null) return emptyList()
        return res.toString(Charsets.UTF_8).lines()
    }

    private fun loadPolicyFromResources(path: String): ByteArray? {
        return classLoader.getResourceAsStream(path)?.readBytes()
    }
}