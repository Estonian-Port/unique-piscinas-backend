package com.estonianport.unique.common.emailService

import com.estonianport.unique.common.errors.BusinessException
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.apache.commons.validator.routines.EmailValidator
import org.springframework.mail.javamail.MimeMessageHelper


@Service
class EmailService {

    @Autowired
    lateinit var sender: JavaMailSender

    fun isEmailValid(target: String): Boolean {
        return target.isNotEmpty() && EmailValidator.getInstance().isValid(target)
    }

    fun sendEmail(emailBody: Email) {
        if(!isEmailValid(emailBody.email)){
            throw BusinessException("Email Invalido")
        }
        sendEmailTool(emailBody.content, emailBody.email, emailBody.subject)
    }

    private fun sendEmailTool(textMessage: String, email: String, subject: String) {
        val message: MimeMessage = sender.createMimeMessage()
        val helper = MimeMessageHelper(message)
        try {
            helper.setTo(email)
            helper.setText(textMessage, true)
            helper.setSubject(subject)
            sender.send(message)
        } catch (e: MessagingException) {
            throw BusinessException("No se pudo enviar el mail")
        }
    }

    fun loadHtmlTemplate(nombreArchivo: String): String {
        val inputStream = javaClass.classLoader.getResourceAsStream("templates/email/$nombreArchivo")
                ?: throw BusinessException("No se encontró la plantilla de mail")
        return inputStream.bufferedReader().use { it.readText() }
    }

    fun renderTemplate(template: String, replacements: Map<String, String>): String {
        var result = template
        for ((key, value) in replacements) {
            result = result.replace("{{${key}}}", value)
        }
        return result
    }



}