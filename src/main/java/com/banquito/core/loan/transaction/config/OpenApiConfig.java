package com.banquito.core.loan.transaction.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8084}")
    private String serverPort;

    @Bean
    public OpenAPI transactionOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:" + serverPort);
        devServer.setDescription("Servidor de desarrollo");

        Contact contact = new Contact();
        contact.setEmail("support@banquito.com");
        contact.setName("Equipo de Desarrollo Banquito");
        contact.setUrl("https://www.banquito.com");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Transaction Service API")
                .version("1.0.0")
                .contact(contact)
                .description(
                        "API para la gestión de transacciones de préstamos de clientes en el sistema bancario Banquito")
                .termsOfService("https://www.banquito.com/terms")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}
