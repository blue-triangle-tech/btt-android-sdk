package com.bluetriangle.android.demo

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket
import java.nio.charset.StandardCharsets
import java.net.URLDecoder
import java.util.HashMap

class DelayResponseHttpServer(private val port: Int) {

    private val serverSocket = ServerSocket(port)

    fun start() {
        println("Server running on port $port")

        val clientSocket = serverSocket.accept()

        handleClient(clientSocket)
    }

    private fun handleClient(clientSocket: java.net.Socket) {
        val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8))
        val out = clientSocket.getOutputStream()

        val requestLine = reader.readLine()
        val path = extractPathFromRequestLine(requestLine)

        val queryParameters = extractQueryParameters(path)
        val delaySeconds = queryParameters["seconds"]?.toLongOrNull() ?: 3

        val response = buildResponse(delaySeconds)

        // Simulate delay
        Thread.sleep(delaySeconds * 1000)

        sendResponse(out, response)

        reader.close()
        out.close()
        clientSocket.close()
    }

    private fun extractPathFromRequestLine(requestLine: String): String {
        return requestLine.split(" ")[1]
    }

    private fun extractQueryParameters(path: String): Map<String, String> {
        val url = URLDecoder.decode(path, StandardCharsets.UTF_8.name())
        val query = url.split("\\?".toRegex(), 2).toTypedArray().getOrNull(1)
        return query?.let {
            parseQueryString(it)
        } ?: emptyMap()
    }

    private fun parseQueryString(query: String): Map<String, String> {
        val params = HashMap<String, String>()
        val pairs = query.split("&")
        for (pair in pairs) {
            val idx = pair.indexOf("=")
            if (idx > 0) {
                val key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name())
                val value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name())
                params[key] = value
            }
        }
        return params
    }

    private fun buildResponse(delaySeconds: Long): String {
        return "{\"message\":\"Delayed for $delaySeconds seconds!\"}"
    }

    private fun sendResponse(out: OutputStream, response: String) {
        val headers = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n"
        val fullResponse = "$headers$response"

        out.write(fullResponse.toByteArray(StandardCharsets.UTF_8))
        out.flush()
    }

    fun stop() {
        serverSocket.close()
    }
}
