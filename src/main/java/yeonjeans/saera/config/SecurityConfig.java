package yeonjeans.saera.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import yeonjeans.saera.exception.CustomAuthenticationEntryPoint;
import yeonjeans.saera.exception.ExceptionHandlerFilter;
import yeonjeans.saera.security.jwt.JwtAuthenticationFilter;
import yeonjeans.saera.security.jwt.TokenProvider;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter(tokenProvider);
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
               "/swagger-ui/**",
                "/v3/api-docs",
                "/h2-console/**"
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .and()
                .oauth2Login();
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class);
        http
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint);

        return http.build();
    }
}
