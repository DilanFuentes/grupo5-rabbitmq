package cl.duoc.inventario;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventarioController {

    // Stock de ejemplo: producto -> unidades disponibles
    private static final Map<String, Integer> STOCK = Map.of(
            "PROD-1", 10,
            "PROD-2", 0,
            "PROD-3", 5);

    @GetMapping("/inventario/{productoId}")
    public Map<String, Object> consultar(@PathVariable String productoId,
                                         @RequestParam(defaultValue = "1") int cantidad) {
        int disponible = STOCK.getOrDefault(productoId, 0);
        return Map.of(
                "productoId", productoId,
                "disponible", disponible,
                "hayStock", disponible >= cantidad);
    }
}
