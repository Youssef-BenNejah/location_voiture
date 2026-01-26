package brama.pressing_api;

import brama.pressing_api.role.Role;
import brama.pressing_api.role.RoleRepository;
import brama.pressing_api.user.User;
import brama.pressing_api.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
public class PressingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PressingApiApplication.class, args);
	}
	@Bean
	public CommandLineRunner commandLineRunner(final RoleRepository roleRepository,
											  final UserRepository userRepository,
											  final PasswordEncoder passwordEncoder,
											  @Value("${app.seed.admin.email:omar.hammamet@carjoy.local}") final String adminEmail,
											  @Value("${app.seed.admin.phone:+21600000000}") final String adminPhone,
											  @Value("${app.seed.admin.password:Admin@12345}") final String adminPassword) {
		return args -> {
			final Optional<Role> userRole = roleRepository.findByName("USER");
			if (userRole.isEmpty()) {
				final Role role = new Role();
				role.setName("USER");
				role.setCreatedBy("admin");
				roleRepository.save(role);
			}
			final Optional<Role> adminRole = roleRepository.findByName("ADMIN");
			if (adminRole.isEmpty()) {
				final Role role = new Role();
				role.setName("ADMIN");
				role.setCreatedBy("admin");
				roleRepository.save(role);
			}

			final Optional<User> existingAdmin = userRepository.findByEmailIgnoreCase(adminEmail);
			if (existingAdmin.isEmpty()) {
				final LocalDateTime now = LocalDateTime.now();
				final User adminUser = User.builder()
						.firstName("Omar")
						.lastName("Hammamet")
						.email(adminEmail)
						.phoneNumber(adminPhone)
						.password(passwordEncoder.encode(adminPassword))
						.enabled(true)
						.locked(false)
						.credentialsExpired(false)
						.emailVerified(true)
						.phoneVerified(true)
						.roles(List.of("ADMIN"))
						.createdDate(now)
						.lastModifiedDate(now)
						.build();
				userRepository.save(adminUser);
			}
		};
	}
}
