package dev.protsenko.securityLinter.utils.image

import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.util.net.HttpConfigurable
import java.io.IOException
import java.net.*
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.concurrent.CompletableFuture

object DockerImageDigestFetcher {
    private val om = ObjectMapper()
    private val httpClient: HttpClient

    init {
        val httpConfigurable = HttpConfigurable.getInstance()
        val builder = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3))

        if (httpConfigurable.USE_HTTP_PROXY) {
            val proxyHost = httpConfigurable.PROXY_HOST
            val proxyPort = httpConfigurable.PROXY_PORT
            val proxyAddress = InetSocketAddress(proxyHost, proxyPort)
            val proxyType =
                if (httpConfigurable.PROXY_TYPE_IS_SOCKS) {
                    Proxy.Type.SOCKS
                } else {
                    Proxy.Type.HTTP
                }
            val proxy = Proxy(proxyType, proxyAddress)
            val proxySelector =
                object : ProxySelector() {
                    override fun select(uri: URI?): List<Proxy> = listOf(proxy)

                    override fun connectFailed(
                        uri: URI?,
                        sa: SocketAddress?,
                        ioe: IOException?,
                    ) {
                        return
                    }
                }

            builder.proxy(proxySelector)

            if (httpConfigurable.PROXY_AUTHENTICATION) {
                val username = httpConfigurable.proxyLogin
                val password = httpConfigurable.plainProxyPassword
                if (username != null && password != null) {
                    builder.authenticator(
                        object : Authenticator() {
                            override fun getPasswordAuthentication(): PasswordAuthentication =
                                PasswordAuthentication(username, password.toCharArray())
                        },
                    )
                }
            }
        }
        httpClient = builder.build()
    }

    fun fetchDigest(imageName: String): CompletableFuture<String> {
        val imagePath =
            if (imageName.contains("/")) {
                imageName
            } else {
                "library/$imageName"
            }

        val apiUrl =
            "https://hub.docker.com/v2/repositories/$imagePath/tags?page_size=1&name=latest"

        val request =
            HttpRequest
                .newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/json")
                .build()

        return httpClient
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply { response ->
                if (response.statusCode() == 200) {
                    val digest = parseDigest(response.body())
                    digest ?: throw RuntimeException("No latest tag found for the specified image.")
                } else {
                    throw RuntimeException("Failed to fetch image digest: ${response.statusCode()}")
                }
            }
    }

    private fun parseDigest(responseBody: String): String? {
        val resultMap = om.readValue(responseBody, Map::class.java)
        val results = resultMap["results"] as? List<*> ?: return null
        val latestResult = results.firstOrNull() as? Map<*, *> ?: return null
        return latestResult["digest"] as? String
    }
}
