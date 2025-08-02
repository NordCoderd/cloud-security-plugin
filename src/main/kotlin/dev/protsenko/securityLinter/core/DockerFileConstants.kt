package dev.protsenko.securityLinter.core

object DockerFileConstants {
    val PROHIBITED_PORTS = setOf(22)
    val PROHIBITED_USERS = setOf("root", "0")
    val POTENTIAL_SECRETS_NAME =
        setOf(
            "PASSWD",
            "PASSWORD",
            "PASS",
            "SECRET",
            "KEY",
            "ACCESS",
            "API_KEY",
            "APIKEY",
            "TOKEN",
            "TKN",
        )
}
