package cl.duoc.pedidos;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "pedidos.exchange";
    public static final String ROUTING_KEY = "pedido.creado";
    public static final String QUEUE_NOTIFICACIONES = "notificaciones.pedido.queue";
    public static final String QUEUE_AUDITORIA = "auditoria.pedido.queue";

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue notificacionesQueue() {
        return new Queue(QUEUE_NOTIFICACIONES, true);
    }

    @Bean
    public Queue auditoriaQueue() {
        return new Queue(QUEUE_AUDITORIA, true);
    }

    @Bean
    public Binding bindingNotificaciones() {
        return BindingBuilder.bind(notificacionesQueue()).to(pedidosExchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding bindingAuditoria() {
        return BindingBuilder.bind(auditoriaQueue()).to(pedidosExchange()).with(ROUTING_KEY);
    }
}
