package pl.czyzlowie.modules.auth;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final LoginSuccessHandler loginSuccessHandler;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${app.security.remember-me.key}")
    private String rememberMeKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName(null);
        http.csrf(csrf -> csrf.csrfTokenRequestHandler(requestHandler));

        http
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/error").permitAll()

                        .requestMatchers(
                                "/profil/**",
                                "/powiadomienia/**",
                                "/dziennik-wypraw/**",
                                "/moje-polowy/**",
                                "/cele/**",
                                "/statystyki/**",
                                "/cele-wedkarskie/**",
                                "/kalendarz-wypraw/**",
                                "/ulubione-miejscowki/**",
                                "/lista-przynet/**",
                                "/moje-zestawy/**",
                                "/checklisty/**",
                                "/notatki/**",
                                "/ustawienia/**"
                        ).authenticated()

                        .anyRequest().permitAll()
                )
                .headers(headers -> headers
                        .cacheControl(cache -> cache.disable())
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(loginSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            String email = request.getParameter("email");
                            request.getSession().setAttribute("SPRING_SECURITY_LAST_USERNAME", email);
                            request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
                            response.sendRedirect("/login?error=true");
                        })
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key(rememberMeKey)
                        .userDetailsService(customUserDetailsService)
                        .tokenValiditySeconds(2592000)
                        .rememberMeParameter("remember-me")
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(loginSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}