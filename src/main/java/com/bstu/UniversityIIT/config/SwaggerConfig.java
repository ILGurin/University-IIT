package com.bstu.UniversityIIT.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    //http://localhost:8080/swagger-ui/index.html
    @Bean
    public OpenAPI api(){
        return new OpenAPI()
                .servers(
                        List.of(
                                new Server()
                                        .url("http://localhost:8080")
                                        .description("URL-адрес сервера в среде разработки"),
                                new Server()
                                        .url("http://localhost:8080")
                                        .description("URL-адрес сервера в продакшене")
                        )
                )
                .info(
                        new Info()
                                .title("Кафедра ИИТ")
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("Name")
                                                .email("Name@gmail.com")
                                )
                );
    }
}