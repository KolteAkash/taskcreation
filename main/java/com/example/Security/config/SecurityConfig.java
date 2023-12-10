package com.example.Security.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
//        http.csrf().ignoringRequestMatchers("http://localhost:3000/**");
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeRequests()
                .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/validator").hasAnyAuthority("ROLE_USER","ROLE_ADMIN","ROLE_SUPER_ADMIN")
                .requestMatchers(HttpMethod.GET, "/projects/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .requestMatchers("/projects/**").hasAnyAuthority("ROLE_ADMIN")
                .requestMatchers("/projects/delete-project/**").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                .requestMatchers("/task/**").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                .requestMatchers("/task/status/**").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                .requestMatchers("/task/update-status-task/**").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                .requestMatchers("/priority/**").hasAnyAuthority("ROLE_ADMIN","ROLE_USER","ROLE_SUPER_ADMIN")
                .requestMatchers("/client/create-user").hasAnyAuthority("ROLE_ADMIN")
                .requestMatchers("/client/**").hasAnyAuthority("ROLE_SUPER_ADMIN")
                .requestMatchers("/admincontroll/**").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                .requestMatchers("/project_type/add-initiative").hasAuthority("ROLE_SUPER_ADMIN")
                .requestMatchers("/worklogs/**").hasAnyAuthority("ROLE_USER","ROLE_ADMIN","ROLE_SUPER_ADMIN")
                .requestMatchers("/epic/create-epic").hasAuthority("ROLE_SUPER_ADMIN")//json file
                .and()
                .csrf().disable()
                .cors(Customizer.withDefaults())
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
