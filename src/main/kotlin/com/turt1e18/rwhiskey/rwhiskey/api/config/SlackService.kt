package com.turt1e18.rwhiskey.rwhiskey.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class SlackService(
    @Value("\${slack.webhook.url:}") private val slackWebhookUrl: String
) {
    private val restTemplate = RestTemplate()

    @org.springframework.scheduling.annotation.Async
    fun sendErrorNotification(method: String, url: String, e: Exception, userIdentifier: String? = null) {
        if (slackWebhookUrl.isBlank()) return

        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        val stackTrace = sw.toString().take(1000)

        val fields = mutableListOf(
            mapOf("title" to "Time", "value" to LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "short" to true),
            mapOf("title" to "API Path", "value" to "[$method] $url", "short" to true),
            mapOf("title" to "User Info", "value" to (userIdentifier ?: "Guest / Unauthenticated"), "short" to true),
            mapOf("title" to "Error Message", "value" to (e.message ?: "No Message"), "short" to false),
            mapOf("title" to "Stack Trace", "value" to "```$stackTrace```", "short" to false)
        )

        val body = mapOf(
            "attachments" to listOf(
                mapOf(
                    "color" to "#ff0000",
                    "title" to "🚨 [R-Whiskey API] 서버 내부 오류 발생",
                    "fields" to fields
                )
            )
        )

        try {
            restTemplate.postForEntity(slackWebhookUrl, body, String::class.java)
        } catch (ex: Exception) {
            println("Slack 알림 전송 실패: ${ex.message}")
        }
    }
}
