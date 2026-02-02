package brama.pressing_api.seed.extra;

import brama.pressing_api.seed.extra.domain.Extra;
import brama.pressing_api.seed.extra.repository.ExtraRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/extras")
@RequiredArgsConstructor
@Tag(name = "Extras", description = "Booking add-ons and extras")
public class ExtraController {
    private final ExtraRepository extraRepository;

    /**
     * Get all active extras
     */
    @GetMapping
    @Operation(summary = "Get all active extras")
    public List<Extra> getAllExtras() {
        return extraRepository.findByActiveTrue();
    }

    /**
     * Get extra by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get extra by ID")
    public Extra getExtraById(@PathVariable String id) {
        return extraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Extra not found"));
    }

    /**
     * Get extras by category
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "Get extras by category")
    public List<Extra> getExtrasByCategory(@PathVariable String category) {
        return extraRepository.findByCategoryAndActiveTrue(category);
    }
}
