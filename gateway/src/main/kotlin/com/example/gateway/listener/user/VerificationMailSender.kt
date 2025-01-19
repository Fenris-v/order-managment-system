package com.example.gateway.listener.user

import com.example.gateway.event.UserRegisteredEvent
import com.example.gateway.service.mail.MailService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

/**
 * Листенер для отправки письма с подтверждением регистрации.
 */
@Component
class VerificationMailSender(private val mailService: MailService) {

    /**
     * Отправляет письмо новому пользователю для подтверждения его почтового адреса.
     */
    @EventListener
    fun onApplicationEvent(event: UserRegisteredEvent) {
        if (event.user.confirmationToken == null) {
            throw NullPointerException("Confirm token is null")
        }

        mailService.sendVerifyMail(event.user)
    }
}
