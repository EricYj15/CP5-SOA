package br.com.fiap3ess.autoescola3ess.config.security;

import br.com.fiap3ess.autoescola3ess.config.security.filter.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity //Este é um padrão mais simples, mas não é o padrão de mercado
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                //Usa a config do CorsConfig
                .cors(Customizer.withDefaults())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/health-check").permitAll()
                                .requestMatchers("/v3/api-docs.yaml",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**").permitAll()
                                /*.requestMatchers(HttpMethod.POST, "/instrutores").hasRole("ADMIN")// Padrão de mercado para gerenciamento de autorizações
                                .requestMatchers(HttpMethod.GET, "/instrutores").hasAnyRole("ADMIN", "USER") // Instrução para autorização individual
                                .requestMatchers(HttpMethod.GET, "/instrutores/{id}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/instrutores").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/instrutores/{id}").hasRole("ADMIN")*/
                                //.requestMatchers("/instrutores").hasRole("ADMIN") // Instrução para grupo de autorização
                                .anyRequest().authenticated()
                )
                //Sem token válido retorna 401
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
