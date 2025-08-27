package brama.pressing_api;

import brama.pressing_api.role.Role;
import brama.pressing_api.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Optional;

@SpringBootApplication
@EnableAsync
public class PressingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PressingApiApplication.class, args);
	}
	@Bean
	public CommandLineRunner commandLineRunner(final RoleRepository roleRepository) {
		return args -> {
			final Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
			if (userRole.isEmpty()) {
				final Role role = new Role();
				role.setName("USER");
				role.setCreatedBy("admin");
				roleRepository.save(role);
			}
		};
	}
}
