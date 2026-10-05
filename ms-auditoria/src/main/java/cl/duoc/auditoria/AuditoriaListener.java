package cl.duoc.auditoria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaListener {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaListener.class);

    @RabbitListener(queues = "auditoria.pedido.queue")
    public void recibir(String mensaje) {
        log.info("[AUDITORIA] Mensaje recibido de auditoria.pedido.queue: {}", mensaje);
        log.info("[AUDITORIA] Registro del pedido guardado (simulado).");
    }
}
