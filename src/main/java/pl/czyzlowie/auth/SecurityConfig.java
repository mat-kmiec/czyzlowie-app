package pl.czyzlowie.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import pl.czyzlowie.modules.auth.CustomOAuth2UserService;
import pl.czyzlowie.modules.auth.LoginSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName(null);
        http.csrf(csrf -> csrf.csrfTokenRequestHandler(requestHandler));

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**", "/webjars/**", "/favicon.ico").permitAll()
                        .requestMatchers("/", "/login", "/register", "/rejestracja", "/error").permitAll()
                        .requestMatchers("/prognoza", "/prognoza/wynik","/barometr", "/ksiezyc", "/ryby/kategoria",
                                "/regulamin", "/o-nas", "/hydro", "/meteo", "/synop", "/mapa", "/wschody-zachody",
                                "/miejscowka/**", "/miejsce-wodowania/**", "/zbiornik-zaporowy/**",
                                "/jezioro/**", "/rzeka/**", "/starorzecze/**", "/lowisko-komercyjne/**",
                                "/lowiska", "/lowiska/**", "/api/map/markers", "/weryfikacja", "/resend-activation").permitAll()
                        .anyRequest().authenticated()
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
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}