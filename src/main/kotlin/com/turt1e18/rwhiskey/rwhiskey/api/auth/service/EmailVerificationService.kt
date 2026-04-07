package com.turt1e18.rwhiskey.rwhiskey.api.auth.service

import com.resend.Resend
import com.resend.services.emails.model.CreateEmailOptions
import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.request.SendVerificationEmailRequest
import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.request.VerifyEmailCodeRequest
import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.response.EmailVerificationResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class EmailVerificationService(
    private val resend: Resend,
    @param:Value("\${resend.from.email}")
    private val fromEmail: String
) {

    private fun generateSixDigitCode(): String {
        return Random.nextInt(100000, 1000000).toString()
    }
    private fun buildVerificationEmailHtml(code: String): String {
        return """
            <div style="font-family: Arial, sans-serif; line-height: 1.6;">
                <h2>R-Whiskey 이메일 인증</h2>
                <p>아래의 확인 코드를 입력해주세요.</p>
                <div style="margin: 24px 0; font-size: 32px; font-weight: bold; letter-spacing: 6px;">
                    $code
                </div>
                <p>해당 코드는 5분안에 인증하셔야 유효합니다.</p>
            </div>
        """.trimIndent()
    }

    fun sendVerificationEmail(
        request: SendVerificationEmailRequest
    ): EmailVerificationResponse {
        val code = generateSixDigitCode()
        val params = CreateEmailOptions.builder()
            .from(fromEmail)
            .to(request.email)
            .subject("[R-Whiskey] 이메일 확인 코드")
            .html(buildVerificationEmailHtml(code))
            .build()

        return try {
            val response = resend.emails().send(params)

            println("성공적으로 전송했습니다.: ${response.id}")
            println("생성된 코드: $code")

            EmailVerificationResponse(
                success = true,
                message = "Verification email sent successfully."
            )
        }catch (e: Exception){
            println("Failed to send verification email: ${e.message}")

            EmailVerificationResponse(
                success = false,
                message = "Failed to send verification email."
            )
        }
    }

    fun verifyEmailCode(
        request: VerifyEmailCodeRequest
    ): EmailVerificationResponse {
        TODO()
    }

}