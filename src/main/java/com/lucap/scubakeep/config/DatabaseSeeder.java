package com.lucap.scubakeep.config;

import com.lucap.scubakeep.entity.Certification;
import com.lucap.scubakeep.entity.DiveLog;
import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.entity.Role;
import com.lucap.scubakeep.repository.DiveLogRepository;
import com.lucap.scubakeep.repository.DiverRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

/**
 * Initializes the database with mock data if it is empty.
 * <p>
 * Implements {@link CommandLineRunner} to execute seeding logic automatically
 * upon application startup. This provides a ready-to-use environment for
 * manual testing.
 */
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final DiverRepository diverRepository;
    private final DiveLogRepository diveLogRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Executes the database seeding process.
     *
     * @param args command line arguments
     * @throws Exception if an error occurs during seeding
     */
    @Override
    public void run(String... args) throws Exception {
        if (diverRepository.count() == 0) {
            LOGGER.info("Empty database detected. Seeding mock data...");

            seedAdminUser();
            Diver luca = seedStandardUser();
            seedDiveLogs(luca);

            LOGGER.info("Mock data successfully seeded!");
        } else {
            LOGGER.info("Database already contains data. Skipping seeder.");
        }
    }

    /**
     * Creates and persists the default system administrator.
     */
    private void seedAdminUser() {
        Diver admin = Diver.builder()
                .username("admin")
                .email("admin@scubakeep.com")
                .password(passwordEncoder.encode("AdminPass123!"))
                .firstName("System")
                .lastName("Admin")
                .countryCode("IT")
                .role(Role.ADMIN)
                .highestCertification(Certification.INSTRUCTOR)
                .profilePicturePath(
                        "https://images.unsplash.com/photo-1544551763-46a013bb70d5" +
                                "?q=80&w=256&auto=format&fit=crop"
                )
                .build();
        diverRepository.save(admin);
    }

    /**
     * Creates and persists a standard mock user for testing.
     *
     * @return the created standard diver entity
     */
    private Diver seedStandardUser() {
        Diver luca = Diver.builder()
                .username("luca")
                .email("luca@scubakeep.com")
                .password(passwordEncoder.encode("UserPass123!"))
                .firstName("Luca")
                .lastName("Diver")
                .countryCode("IT")
                .role(Role.USER)
                .highestCertification(Certification.DIVEMASTER)
                .specialties(Set.of("Night Diver", "Deep Diver", "Wreck Diver"))
                .profilePicturePath("https://github.com/luca-pal.png")
                .build();
        return diverRepository.save(luca);
    }

    /**
     * Creates and persists sample dive logs for the specified diver.
     *
     * @param diver the owner of the mock dive logs
     */
    private void seedDiveLogs(Diver diver) {
        DiveLog log1 = DiveLog.builder()
                .diveDate(LocalDate.of(2023, 8, 15))
                .location("Red Sea, Egypt")
                .diveSite("Thistlegorm Wreck")
                .maxDepth(30.5)
                .duration(45)
                .diveBuddy("John Doe")
                .notes("Amazing wreck dive! Saw a turtle.")
                .diver(diver)
                .build();

        DiveLog log2 = DiveLog.builder()
                .diveDate(LocalDate.of(2023, 8, 16))
                .location("Red Sea, Egypt")
                .diveSite("Shark & Yolanda Reef")
                .maxDepth(25.0)
                .duration(50)
                .diveBuddy("John Doe")
                .notes("Lots of beautiful corals. John got eaten by a tiger shark.")
                .diver(diver)
                .build();

        diveLogRepository.save(log1);
        diveLogRepository.save(log2);
    }
}