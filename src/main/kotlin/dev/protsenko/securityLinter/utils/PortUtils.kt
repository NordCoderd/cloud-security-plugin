package dev.protsenko.securityLinter.utils

object PortUtils {
    fun parseContainerPorts(portSpec: String): List<Int> {
        // Quick check: empty or blank input
        if (portSpec.isBlank()) return emptyList()

        // 1) Manually trim leading/trailing whitespace to avoid creating intermediate strings
        var start = 0
        var end = portSpec.length - 1

        while (start <= end && portSpec[start].isWhitespace()) start++
        while (end >= start && portSpec[end].isWhitespace()) end--

        // If nothing left after trimming
        if (start > end) return emptyList()

        // 2) Strip protocol: find slash (e.g. "/tcp"), ignore everything after the first slash
        val slashIndex = portSpec.indexOf('/', start)
        val effectiveEnd = if (slashIndex == -1 || slashIndex > end) end else slashIndex - 1

        // If the slash is at or before 'start', it means there's effectively no real port spec
        if (effectiveEnd < start) return emptyList()

        // 3) Extract the substring we care about (without protocol and extra whitespace)
        //    This is e.g. "127.0.0.1:8000:3000", "8000:3000", or just "3000"
        val coreSpec = portSpec.substring(start, effectiveEnd + 1)

        // 4) Find the last colon to isolate the container port part
        val lastColonIndex = coreSpec.lastIndexOf(':')
        val containerPart =
            if (lastColonIndex == -1) {
                // No colon => entire coreSpec is the container port
                coreSpec
            } else {
                // The container port is after the last colon
                coreSpec.substring(lastColonIndex + 1)
            }

        // 5) Check if containerPart is a single port or a range ("-")
        val dashIndex = containerPart.indexOf('-')
        return if (dashIndex == -1) {
            // Single port
            val singlePort = containerPart.toIntOrNull() ?: return emptyList()
            listOf(singlePort)
        } else {
            // Range of ports, e.g. "8080-8083"
            val startPort = containerPart.substring(0, dashIndex).toIntOrNull() ?: return emptyList()
            val endPort = containerPart.substring(dashIndex + 1).toIntOrNull() ?: return emptyList()
            if (startPort > endPort) return emptyList()

            // Build the range [startPort..endPort]
            val size = endPort - startPort + 1
            val ports = ArrayList<Int>(size)
            for (p in startPort..endPort) {
                ports.add(p)
            }
            ports
        }
    }
}
