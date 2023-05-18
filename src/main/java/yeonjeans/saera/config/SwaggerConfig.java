package yeonjeans.saera.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "bearerAuth",
        bearerFormat = "JWT",
        scheme = "bearer")
public class SwaggerConfig {

}
