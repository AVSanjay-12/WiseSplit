package com.sanjay.splitwise.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI splitwiseOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Splitwise Backend API")
                        .description("Backend APIs for Splitwise-style expense sharing system")
                        .version("1.0"));
    }
}