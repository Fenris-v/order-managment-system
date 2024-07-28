package com.example.gateway.service.mail

import com.example.gateway.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

/**
 * Сервис отправки писем.
 */
@Service
class MailService(
    private val javaMailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
    @Value("\${spring.mail.email}") private val email: String,
    @Value("\${spring.mail.name}") private val name: String,
    @Value("\${app.domain}") private val domain: String
) {
    companion object {
        private const val ENCODING = "UTF-8"
        private const val VERIFY_SUBJECT = "Подтверждение регистрации"
        private const val PASSWORD_RESET_SUBJECT = "Новый пароль"
        private const val CHANGE_EMAIL_SUBJECT = "Подтверждение изменения email"
        private const val CHANGE_EMAIL_URI = "%s/api/v1/user/email?token=%s"
        private const val VERIFY_URI = "%s/api/v1/user/verify?token=%s"
    }

    /**
     * Отправить письмо пользователю.
     *
     * @param to Адрес получателя.
     * @param subject Тема письма.
     * @param body Тело письма.
     */
    fun send(to: String, subject: String, body: String) {
        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, ENCODING)

        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(body, true)
        helper.setFrom(email, name)

        javaMailSender.send(message)
    }

    /**
     * Отправить пользователю письмо с новым паролем.
     *
     * @param user Пользователь.
     * @param password Новый пароль.
     */
    @Async("asyncMailExecutor")
    fun sendPasswordResetMail(user: User, password: String) {
        send(user.email, PASSWORD_RESET_SUBJECT, getPasswordResetMailHtml(password))
    }

    private fun getPasswordResetMailHtml(password: String): String {
        val context = Context()
        context.setVariable("password", password)
        context.setVariable("subject", PASSWORD_RESET_SUBJECT)

        return templateEngine.process("password_reset_email", context)
    }

    /**
     * Отправить пользователю письмо для подтверждения аккаунта.
     *
     * @param user Пользователь.
     */
    @Async("asyncMailExecutor")
    fun sendVerifyMail(user: User) {
        send(user.email, VERIFY_SUBJECT, getVerifyMailHtml(user.confirmationToken))
    }

    private fun getVerifyMailHtml(token: String?): String {
        val context = Context()
        context.setVariable("href", String.format(VERIFY_URI, domain, token))
        context.setVariable("subject", VERIFY_SUBJECT)

        return templateEngine.process("verified_email", context)
    }

    /**
     * Отправить пользователю письмо для подтверждения смены email.
     *
     * @param user Пользователь.
     */
    @Async("asyncMailExecutor")
    fun sendChangeEmailLetter(user: User, newEmail: String) {
        send(newEmail, CHANGE_EMAIL_SUBJECT, getChangeMailLetterHtml(user.confirmationToken))
    }

    private fun getChangeMailLetterHtml(token: String?): String {
        val context = Context()
        context.setVariable("href", String.format(CHANGE_EMAIL_URI, domain, token))
        context.setVariable("subject", VERIFY_SUBJECT)
        context.setVariable("email", email)

        return templateEngine.process("change_email", context)
    }
}
