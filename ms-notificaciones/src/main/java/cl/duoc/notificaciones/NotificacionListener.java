package cl.duoc.notificaciones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificacionListener {

    private static final Logger log = LoggerFactory.getLogger(NotificacionListener.class);

    @RabbitListener(queues = "notificaciones.pedido.queue")
    public void recibir(String mensaje) {
        log.info("[NOTIFICACIONES] Mensaje recibido de notificaciones.pedido.queue: {}", mensaje);
        log.info("[NOTIFICACIONES] Simulando envío de correo al cliente...");
    }
}
