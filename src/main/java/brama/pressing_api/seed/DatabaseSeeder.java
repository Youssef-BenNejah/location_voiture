package brama.pressing_api.seed;

import brama.pressing_api.location.domain.model.Location;

import brama.pressing_api.seed.extra.domain.Extra;
import brama.pressing_api.seed.extra.repository.ExtraRepository;
import brama.pressing_api.seed.location_seed.domain.LocationSeed;
import brama.pressing_api.seed.location_seed.repository.LocationSeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Seeds initial data for locations and extras if they don't exist.
 * Runs automatically on application startup.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final LocationSeedRepository locationRepository;
    private final ExtraRepository extraRepository;

    @Override
    public void run(String... args) {
        log.info("🌱 Starting database seeding...");
        seedLocations();
        seedExtras();
        log.info("✅ Database seeding completed!");
    }

    private void seedLocations() {
        // Check if locations already exist
        if (locationRepository.count() > 0) {
            log.info("📍 Locations already exist, skipping seed");
            return;
        }

        log.info("📍 Seeding locations...");

        List<LocationSeed> locations = List.of(
                LocationSeed.builder()
                        .id("1")
                        .name("Downtown Office")
                        .address("123 Main Street")
                        .city("New York")
                        .state("NY")
                        .country("USA")
                        .postalCode("10001")
                        .active(true)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build(),

                LocationSeed.builder()
                        .id("2")
                        .name("Airport Terminal")
                        .address("456 Airport Road")
                        .city("New York")
                        .state("NY")
                        .country("USA")
                        .postalCode("11430")
                        .active(true)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build(),

                LocationSeed.builder()
                        .id("3")
                        .name("North Station")
                        .address("789 North Avenue")
                        .city("New York")
                        .state("NY")
                        .country("USA")
                        .postalCode("10002")
                        .active(true)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build(),

                LocationSeed.builder()
                        .id("4")
                        .name("South Branch")
                        .address("321 South Boulevard")
                        .city("New York")
                        .state("NY")
                        .country("USA")
                        .postalCode("10003")
                        .active(true)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build()
        );

        locationRepository.saveAll(locations);
        log.info("✅ Seeded {} locations", locations.size());
    }

    private void seedExtras() {
        // Check if extras already exist
        if (extraRepository.count() > 0) {
            log.info("🎁 Extras already exist, skipping seed");
            return;
        }

        log.info("🎁 Seeding extras...");

        List<Extra> extras = List.of(
                Extra.builder()
                        .id("additional-driver")
                        .name("Additional Driver")
                        .description("Add an additional authorized driver to your booking")
                        .pricePerDay(new BigDecimal("15.00"))
                        .category("DRIVER")
                        .active(true)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build(),

                Extra.builder()
                        .id("child-seat")
                        .name("Child Safety Seat")
                        .description("Child safety seat suitable for ages 1-4")
                        .pricePerDay(new BigDecimal("10.00"))
                        .category("SAFETY")
                        .active(true)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build(),

                Extra.builder()
                        .id("gps-navigation")
                        .name("GPS Navigation System")
                        .description("Built-in GPS navigation system")
                        .pricePerDay(new BigDecimal("8.00"))
                        .category("ELECTRONICS")
                        .active(true)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build(),

                Extra.builder()
                        .id("premium-insurance")
                        .name("Premium Insurance")
                        .description("Comprehensive coverage with zero deductible")
                        .pricePerDay(new BigDecimal("35.00"))
                        .category("INSURANCE")
                        .active(true)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build(),

                Extra.builder()
                        .id("wifi-hotspot")
                        .name("Mobile WiFi Hotspot")
                        .description("Stay connected with 4G mobile hotspot")
                        .pricePerDay(new BigDecimal("12.00"))
                        .category("ELECTRONICS")
                        .active(true)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build()
        );

        extraRepository.saveAll(extras);
        log.info("✅ Seeded {} extras", extras.size());
    }
}