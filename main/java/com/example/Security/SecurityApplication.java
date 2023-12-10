package com.example.Security;

import com.example.Security.model.Role;
import com.example.Security.model.User;
import com.example.Security.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;

@SpringBootApplication
@EnableWebSecurity
@EnableJpaRepositories
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}
//	@Bean
//	BCryptPasswordEncoder brBCryptPasswordEncoder(){
//		return new BCryptPasswordEncoder();
//	}
//	@Bean
//	CommandLineRunner run(UserService userService){
//		return args -> {
//			userService.saveRole(new Role(null,"ROLE_USER","this is user","default","default"));
//			userService.saveRole(new Role(null,"ROLE_MANAGER","this is manager","default","default"));
//			userService.saveRole(new Role(null,"ROLE_ADMIN","this is Admin","default","default"));
//			userService.saveRole(new Role(null,"ROLE_SUPER_ADMIN","this is Super admin","default","default"));
//
//
//
//			userService.saveUser(new User("234972340234","superAdmin","superadmin@taskmanager.com","pass",new HashSet<>(),"default","default"));
//			userService.saveUser(new User("234972340234","ftbColabAdmin","kedar@ftbcolab.com","pass",new HashSet<>(),"default","default"));
//			userService.saveUser(new User("234972340234","ftbCommunicationAdmin","moin@ftbCommunication.com","pass",new HashSet<>(),"default","default"));
//
//
//
//			userService.addToUser("superadmin@taskmanager.com","ROLE_USER");
//			userService.addToUser("kedar@ftbcolab.com","ROLE_ADMIN");
//			userService.addToUser("moin@ftbCommunication.com","ROLE_ADMIN");
//
//			userService.addToUser("superadmin@taskmanager.com","ROLE_SUPER_ADMIN");
//
//		};
//	}
}
