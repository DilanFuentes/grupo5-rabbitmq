package cl.duoc.pedidos;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class PedidoController {

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);
    private static final long PRECIO_UNITARIO = 12990;

    private final AtomicLong contador = new AtomicLong(1000);
    private final RestClient inventarioClient;
    private final PedidoEventPublisher publisher;

    public PedidoController(PedidoEventPublisher publisher,
                            @Value("${inventario.url}") String inventarioUrl) {
        this.publisher = publisher;
        this.inventarioClient = RestClient.create(inventarioUrl);
    }

    public record PedidoRequest(String productoId, int cantidad, String clienteEmail) {}

    @PostMapping("/pedidos")
    public ResponseEntity<Map<String, Object>> crear(@RequestBody PedidoRequest pedido) {
        log.info("Pedido recibido: {} x{} de {}", pedido.productoId(), pedido.cantidad(), pedido.clienteEmail());

        // 1) Comunicación SÍNCRONA: necesito la respuesta para decidir si acepto el pedido
        log.info("Consultando stock a ms-inventario (REST)...");
        Map<?, ?> stock = inventarioClient.get()
                .uri("/inventario/{id}?cantidad={c}", pedido.productoId(), pedido.cantidad())
                .retrieve()
                .body(Map.class);
        boolean hayStock = stock != null && Boolean.TRUE.equals(stock.get("hayStock"));

        if (!hayStock) {
            log.warn("Pedido rechazado: sin stock de {}", pedido.productoId());
            return ResponseEntity.status(409)
                    .body(Map.of("error", "Sin stock suficiente", "productoId", pedido.productoId()));
        }

        // 2) Pedido aceptado
        long pedidoId = contador.incrementAndGet();
        long total = pedido.cantidad() * PRECIO_UNITARIO;
        String fecha = LocalDateTime.now().withNano(0).toString();

        // 3) Comunicación ASÍNCRONA: publico el evento y sigo, sin esperar a nadie
        String payload = String.format(
                "{\"pedidoId\":\"%d\",\"clienteEmail\":\"%s\",\"total\":%d,\"fechaCreacion\":\"%s\"}",
                pedidoId, pedido.clienteEmail(), total, fecha);
        publisher.publicarPedidoCreado(payload);
        log.info("Evento PedidoCreado publicado en {} con routing key {}: {}",
                RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, payload);

        return ResponseEntity.status(201)
                .body(Map.of("pedidoId", pedidoId, "estado", "CREADO", "total", total));
    }
}
