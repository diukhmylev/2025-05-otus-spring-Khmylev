package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService myUserDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.myUserDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login", "/error", "/403").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/", "/authors", "/genres",
                                "/comments/book/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/comments/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST,"/*/delete").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/*/edit", "/new").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/authors/new", "/authors/*/edit", "/authors/*/delete").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/authors/*/delete").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/genres/new", "/genres/*/edit").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/genres/*/delete").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/403")
                )
                .formLogin(withDefaults())
                .logout(withDefaults());

        return http.build();
    }



    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(myUserDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return new ProviderManager(provider);
    }
}
