package es.daw.bibliotecajdbc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // AUTORIZACIÓN: qué rutas son públicas
                .authorizeHttpRequests(auth -> auth
                        // Públicas: portada, página de error y recursos estáticos
                        .requestMatchers("/", "/error", "/css/**", "/vendor/**").permitAll()
                        // Consola H2: solo para desarrollo
                        .requestMatchers("/h2-console/**").permitAll()
                        // Cualquier otra cosa: estar identificado
                        .anyRequest().authenticated()
                )
                // AUTENTICACIÓN: formulario de login propio
                .formLogin(form -> form
                        .loginPage("/login")              // GET /login: nuestra vis ta
                        .defaultSuccessUrl("/libros")     // a dónde ir tras entrar
                        .permitAll()
                )
                // LOGOUT: POST /logout (con token CSRF)
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                // La consola H2 no envía token CSRF y se muestra dentro de un <iframe>
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}