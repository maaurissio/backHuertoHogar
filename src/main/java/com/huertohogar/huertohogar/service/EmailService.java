package com.huertohogar.huertohogar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@huertohogar.com}")
    private String fromEmail;

    public void enviarCodigoRecuperacion(String destinatario, String nombre, String codigo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(destinatario);
            message.setSubject("Huerto Hogar - Código de recuperación de contraseña");
            message.setText(String.format(
                    "Hola %s,\n\n" +
                    "Has solicitado restablecer tu contraseña en Huerto Hogar.\n\n" +
                    "Tu código de verificación es: %s\n\n" +
                    "Este código expirará en 15 minutos.\n\n" +
                    "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                    "Saludos,\n" +
                    "El equipo de Huerto Hogar",
                    nombre, codigo
            ));

            mailSender.send(message);
            log.info("Email de recuperación enviado a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error enviando email: {}", e.getMessage());
            throw new RuntimeException("Error enviando email", e);
        }
    }

    public void enviarNotificacionPedido(String destinatario, String nombre, String pedidoId) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(destinatario);
            message.setSubject("Huerto Hogar - Confirmación de pedido #" + pedidoId);
            message.setText(String.format(
                    "Hola %s,\n\n" +
                    "¡Gracias por tu compra en Huerto Hogar!\n\n" +
                    "Tu pedido #%s ha sido confirmado y está siendo procesado.\n\n" +
                    "Te notificaremos cuando tu pedido sea enviado.\n\n" +
                    "Saludos,\n" +
                    "El equipo de Huerto Hogar",
                    nombre, pedidoId
            ));

            mailSender.send(message);
            log.info("Email de confirmación de pedido enviado a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error enviando email de confirmación: {}", e.getMessage());
        }
    }
}
