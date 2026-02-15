package com.agrosoft.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Employees API - AgroSoft",
                version = "1.0.0",
                description = "The AgroSoft API provides endpoints to manage the company's core operations in an agroindustrial environment.\n\n"
                        + "Modules included:\n"
                        + "- Users: authentication, authorization, and profile management.\n"
                        + "- Employees: CRUD operations for employees including personal data, contract details, and employment status.\n"
                        + "- Animals: management of livestock and other animals.\n"
                        + "- Machines & Vehicles: operational status and maintenance costs.\n"
                        + "- Financial: recording and reporting of income and expenses.\n\n"
                        + "This API is designed for full-stack development practice and simulates a real corporate environment."
        )
)
public class OpenApiConfig {
}
