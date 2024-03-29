package com.bstu.UniversityIIT;

import com.bstu.UniversityIIT.auth.AuthenticationService;
import com.bstu.UniversityIIT.auth.RegisterRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.bstu.UniversityIIT.entity.Role.*;

@SpringBootApplication
public class UniversityIitApplication {

	public static void main(String[] args) {
		SpringApplication.run(UniversityIitApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service
	){
		return args -> {
			var user = RegisterRequest.builder()
					.username("user")
					.password("user")
					.role(USER)
					.build();
			System.out.println("User token: " + service.register(user).getAccessToken());

			var teacher = RegisterRequest.builder()
					.username("teacher")
					.password("teacher")
					.role(TEACHER)
					.build();
			System.out.println("Teacher token: " + service.register(teacher).getAccessToken());

			var admin = RegisterRequest.builder()
					.username("admin")
					.password("admin")
					.role(ADMIN)
					.build();
			System.out.println("Admin token: " + service.register(admin).getAccessToken());
		};
	}
}
